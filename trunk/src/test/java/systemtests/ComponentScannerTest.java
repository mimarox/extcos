package systemtests;

import static org.testng.Assert.assertEquals;

import java.io.Serializable;
import java.util.Set;

import javax.swing.JComponent;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.ComponentScanner;
import net.sf.extcos.internal.ArraySet;

import org.testng.annotations.Test;

import resources.annotations.State;
import resources.classes.generic.TestInterface;

import common.TestBase;

public class ComponentScannerTest extends TestBase {
	
	@Test
	public void testGetClasses() {
		ComponentScanner scanner = new ComponentScanner();
		
		final Set<Class<? extends TestInterface>> implementingStore =
			new ArraySet<Class<? extends TestInterface>>();
		
		final Set<Class<?>> annotatedStore = new ArraySet<Class<?>>();
		
		final Set<Class<? extends JComponent>> jComponentStore =
			new ArraySet<Class<? extends JComponent>>();
		
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
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
		ComponentScanner scanner = new ComponentScanner();
		
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
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
		ComponentScanner scanner = new ComponentScanner();
		
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
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
		ComponentScanner scanner = new ComponentScanner();
		
		Set<Class<?>> classes = scanner.getClasses(new ComponentQuery() {
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
}