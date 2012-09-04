package systemtests;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;

import net.sf.extcos.classgeneration.ClassGenerationListener;
import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.internal.JavaClassResourceType;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.Package;
import net.sf.extcos.spi.QueryContext;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.PropertyInjector;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import resources.all.jar.classes.in.use.generic.TestInterface;
import resources.annotations.EnumBasedAnnotation;
import resources.annotations.State;
import resources.annotations.TestInvokable;

import common.TestBase;

public class ClassGenerationAndUseTest extends TestBase {
	private class Loader extends ClassLoader { /* empty class body */ }

	private ResourceResolver resolver;
	private final Set<Class<?>> classes = new HashSet<Class<?>>();
	private final ClassGenerationListener listener = new ClassGenerationListener() {
		@Override
		public <T> void classGenerated(final Class<? extends T> clazz) {
			classes.add(clazz);
		}
	};

	@BeforeClass
	public void initResolver() throws NoSuchFieldException {
		resolver = new ResourceResolver();
		PropertyInjector injector = new PropertyInjector();
		injector.setTarget(resolver);
		injector.inject("classLoader", Thread.currentThread().getContextClassLoader());
	}

	@BeforeMethod
	public void initClassLoader() {
		QueryContext.getInstance().setClassLoader(new Loader());
	}

	@BeforeMethod
	public void initClasses() {
		classes.clear();
	}

	@Test(dataProvider = "resources")
	public void testGetRootFilteredClasses(final Set<Resource> resources) throws Exception {
		for (Resource resource : resources) {
			try {
				if (resource.isClass()) {
					resource.addClassGenerationListener(listener);
					resource.generateAndDispatchClass();
				}
			} catch (ConcurrentInspectionException ignored) {
				// can be ignored, because we're not inspecting the resources concurrently
			}
		}

		assertEquals(classes.size(), getIntProperty(
				"classes.rootFiltered.amount"));

		int illegalAccessExceptionCounter = 0;
		int invokableMethodCounter = 0;

		for (Class<?> clazz : classes) {
			try {
				Object object = clazz.newInstance();
				Method[] methods = clazz.getMethods();

				for (Method method : methods) {
					if (method.isAnnotationPresent(TestInvokable.class)) {
						method.invoke(object);
						invokableMethodCounter++;
					}
				}
			} catch (IllegalAccessException e) {
				illegalAccessExceptionCounter++;

				Constructor<?> constructor = clazz.getConstructor();
				constructor.setAccessible(true);
				constructor.newInstance();
			}
		}

		assertEquals(illegalAccessExceptionCounter, getIntProperty("classes.notAccessible.amount"));
		assertEquals(invokableMethodCounter, getIntProperty("classes.invokableMethod.amount"));
	}

	@Test(dataProvider = "resources")
	public void testGetAnnotatedWithStateClasses(final Set<Resource> resources) {
		for (Resource resource : resources) {
			try {
				if (resource.isClass() &&
						resource.getAnnotationMetadata(State.class) != null) {
					resource.addClassGenerationListener(listener);
					resource.generateAndDispatchClass();
				}
			} catch (ConcurrentInspectionException ignored) {
				// can be ignored, because we're not inspecting the resources concurrently
			}
		}

		assertEquals(classes.size(), getIntProperty(
				"classes.annotatedWith.State.amount"));
	}

	@Test(dataProvider = "resources")
	public void testGetAnnotatedWithEnumBasedClasses(final Set<Resource> resources) {
		for (Resource resource : resources) {
			try {
				if (resource.isClass() &&
						resource.getAnnotationMetadata(
								EnumBasedAnnotation.class) != null) {
					resource.addClassGenerationListener(listener);
					resource.generateAndDispatchClass();
				}
			} catch (ConcurrentInspectionException ignored) {
				// can be ignored, because we're not inspecting the resources concurrently
			}
		}

		assertEquals(classes.size(), getIntProperty(
				"classes.annotatedWith.EnumBasedAnnotation.amount"));
	}

	@Test(dataProvider = "resources")
	public void testGetImplementingClasses(final Set<Resource> resources) {
		for (Resource resource : resources) {
			try {
				if (resource.isClass() &&
						resource.hasInterface(TestInterface.class)) {
					resource.addClassGenerationListener(listener);
					resource.generateAndDispatchClass();
				}
			} catch (ConcurrentInspectionException ignored) {
				// can be ignored, because we're not inspecting the resources concurrently
			}
		}

		assertEquals(classes.size(), getIntProperty(
				"classes.implementing.TestInterface.amount"));
	}

	@Test(dataProvider = "resources")
	public void testGetExtendingClasses(final Set<Resource> resources) {
		for (Resource resource : resources) {
			try {
				if (resource.isClass() &&
						resource.isSubclassOf(JComponent.class)) {
					resource.addClassGenerationListener(listener);
					resource.generateAndDispatchClass();
				}
			} catch (ConcurrentInspectionException ignored) {
				// can be ignored, because we're not inspecting the resources concurrently
			}
		}

		assertEquals(classes.size(), getIntProperty(
				"classes.extending.JComponent.amount"));
	}

	@DataProvider(name = "resources")
	public Object[][] getResources() {
		Set<ResourceType> resourceTypes = new HashSet<ResourceType>();
		resourceTypes.add(JavaClassResourceType.javaClasses());

		Package basePackage = new Package(getProperty("resources.package"));

		return new Object[][]{{
			resolver.getResources(resourceTypes, basePackage)
		}};
	}
}
