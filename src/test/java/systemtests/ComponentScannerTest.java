package systemtests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.Serializable;
import java.util.Set;

import javax.swing.JComponent;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;
import net.sf.extcos.internal.ArraySet;

import org.testng.annotations.Test;

import resources.all.jar.classes.in.use.annotated.multi.MultipleAnnotationsClass;
import resources.all.jar.classes.in.use.generic.TestInterface;
import resources.all.test.classes.in.use.annotated.inherited.InheritedAnnotationClass;
import resources.annotations.First;
import resources.annotations.Second;
import resources.annotations.State;

import common.TestBase;

public class ComponentScannerTest extends TestBase {
	private ComponentScanner scanner = new ComponentScanner();
	
	@Test
	public void testGetClasses() {
		final Set<Class<? extends TestInterface>> implementingStore =
				new ArraySet<Class<? extends TestInterface>>();

		final Set<Class<?>> annotatedStore = new ArraySet<Class<?>>();

		final Set<Class<? extends JComponent>> jComponentStore =
				new ArraySet<Class<? extends JComponent>>();

		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				andStore(thoseImplementing(TestInterface.class).into(implementingStore),
						thoseAnnotatedWith(State.class).into(annotatedStore),
						thoseExtending(JComponent.class).into(jComponentStore)).
						returning(allExtending(Object.class));
			}
		});

		System.out.println("TestInterface implementors: " + implementingStore);
		System.out.println("Annotated with State:       " + annotatedStore);
		System.out.println("Object extenders:           " + classes);
		System.out.println("JComponent extenders:       " + jComponentStore);

		assertEquals(classes.size(), getIntProperty("classes.rootFiltered.amount"));
		assertEquals(jComponentStore.size(), getIntProperty("classes.extending.JComponent.amount"));
		assertEquals(annotatedStore.size(), getIntProperty("classes.annotatedWith.State.amount"));
		assertEquals(implementingStore.size(), getIntProperty("classes.implementing.TestInterface.amount"));
	}

	@Test
	public void testGetImplementingClasses() {
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				returning(allImplementing(TestInterface.class));
			}
		});

		assertEquals(classes.size(), getIntProperty("classes.implementing.TestInterface.amount"));
	}

	@Test
	public void testGetMultiImplementingClasses() {
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				returning(allImplementing(TestInterface.class, Serializable.class));
			}
		});

		assertEquals(classes.size(), getIntProperty("classes.implementing.TestInterface_Serializable.amount"));
	}

	@Test
	public void testGetImplementingAndExtendingClasses() {
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				returning(allBeing(
						and(
								subclassOf(JComponent.class),
								implementorOf(TestInterface.class))));
			}
		});

		assertEquals(classes.size(), 1);
	}

	@Test
	public void testAndStoreNoReturning() {
		final Set<Class<? extends TestInterface>> store =
				new ArraySet<Class<? extends TestInterface>>();

		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				andStore(thoseImplementing(TestInterface.class).into(store));
			}
		});

		assertEquals(store.size(), getIntProperty("classes.implementing.TestInterface.amount"));
		assertEquals(classes.size(), 0);
	}

	@Test
	public void testGetMultipleAnnotationsClasses() {
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				returning(allBeing(and(
						annotatedWith(First.class),
						annotatedWith(Second.class))));
			}
		});

		assertTrue(classes.contains(MultipleAnnotationsClass.class));
	}

	@Test
	public void testGetInheritedAnnotationClasses() {
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				returning(allAnnotatedWith(First.class));
			}
		});

		assertTrue(classes.contains(MultipleAnnotationsClass.class));
		assertTrue(classes.contains(InheritedAnnotationClass.class));
	}

	@Test
	public void testReturningAll() {
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().from(getProperty("resources.package")).returning(all());
			}
		});

		assertEquals(classes.size(), getIntProperty("classes.rootFiltered.amount"));
	}
	
	@Test
	public void testOverlappingPackagePatternReturningAll() {
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(
					getProperty("resources.package"),
					getProperty("resources.overlapping.package")).
				returning(all());
			}
		});

		assertEquals(classes.size(), getIntProperty("classes.rootFiltered.amount"));
	}
	
	@Test(dependsOnMethods = "testGetClasses")
	public void testGetEnums() {
		Set<Class<?>> enums = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				includingEnums().
				from(getProperty("resources.package")).
				returning(allExtending(Enum.class));
			}
		});
		
		assertEquals(enums.size(), getIntProperty("enums.all.amount"));
	}
	
	@Test(dependsOnMethods = "testGetEnums")
	public void testGetNoEnums() {
		Set<Class<?>> enums = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				from(getProperty("resources.package")).
				returning(allExtending(Enum.class));
			}
		});
		
		assertEquals(enums.size(), 0);
	}
	
	@Test
	public void testGetEnumsImplementingInterface() {
		Set<Class<?>> enums = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				includingEnums().
				from(getProperty("resources.package")).
				returning(allBeing(
						and(
							subclassOf(Enum.class),
							implementorOf(TestInterface.class)
							)
						));
			}
		});
		
		assertEquals(enums.size(), getIntProperty("enums.implementing.TestInterface.amount"));
	}
	
	@Test
	public void testGetAllImplementingInterface() {
		Set<Class<?>> enums = scanner.getClasses(new ComponentQuery() {
			@Override
			protected void query() {
				select().
				includingEnums().
				from(getProperty("resources.package")).
				returning(allImplementing(TestInterface.class));
			}
		});
		
		assertEquals(enums.size(), getIntProperty("all.implementing.TestInterface.amount"));
	}
}