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
import net.sf.extcos.internal.PackageImpl;
import net.sf.extcos.internal.ResourceResolverImpl;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.Package;
import net.sf.extcos.spi.ClassLoaderHolder;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.PropertyInjector;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import resources.annotations.EnumBasedAnnotation;
import resources.annotations.State;
import resources.annotations.TestInvokable;
import resources.classes.generic.TestInterface;

import common.TestBase;

public class ClassGenerationAndUseTest extends TestBase {
	private class Loader extends ClassLoader {}
	
	private ResourceResolver resolver;
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	private ClassGenerationListener listener = new ClassGenerationListener() {
		public <T> void classGenerated(Class<? extends T> clazz) {
			classes.add(clazz);
		}
	};
	
	@BeforeClass
	public void initResolver() throws NoSuchFieldException {
		resolver = new ResourceResolverImpl();
		PropertyInjector injector = new PropertyInjector();
		injector.setTarget(resolver);
		injector.inject("classLoader", Thread.currentThread().getContextClassLoader());
	}
	
	@BeforeMethod
	public void initClassLoader() throws Exception {
		ClassLoaderHolder.setClassLoader(new Loader());
	}
	
	@BeforeMethod
	public void initClasses() {
		classes.clear();
	}
	
	@Test
	public void testGetRootFilteredClasses() throws Exception {
		Set<Resource> resources = getResources();
		
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
	
	@Test
	public void testGetAnnotatedWithStateClasses() {
		Set<Resource> resources = getResources();
		
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
	
	@Test
	public void testGetAnnotatedWithEnumBasedClasses() {
		Set<Resource> resources = getResources();
		
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
	
	@Test
	public void testGetImplementingClasses() {
		Set<Resource> resources = getResources();
		
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
	
	@Test
	public void testGetExtendingClasses() {
		Set<Resource> resources = getResources();
		
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
	
	private Set<Resource> getResources() {
		Set<ResourceType> resourceTypes = new HashSet<ResourceType>();
		resourceTypes.add(JavaClassResourceType.javaClasses());
		
		Package basePackage = new PackageImpl(getProperty("resources.package"));
		
		return resolver.getResources(resourceTypes, basePackage);
	}
}
