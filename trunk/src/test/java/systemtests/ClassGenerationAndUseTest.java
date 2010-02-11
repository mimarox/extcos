package systemtests;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.classgeneration.ClassGenerationListener;
import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.internal.JavaClassResourceType;
import net.sf.extcos.internal.PackageImpl;
import net.sf.extcos.internal.ResourceResolverImpl;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.Package;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.PropertyInjector;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import resources.annotations.State;
import resources.annotations.TestInvokable;

import common.TestBase;

public class ClassGenerationAndUseTest extends TestBase {
	private ResourceResolver resolver;
	private Set<Class<?>> classes = new ArraySet<Class<?>>();
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
	public void initClasses() {
		classes.clear();
	}
	
	@Test
	public void testGetClasses() {
		Set<Resource> resources = getResources();
		
		for (Resource resource : resources) {
			resource.addClassGenerationListener(listener);
			resource.generateAndDispatchClass();
		}
		
		assertEquals(classes.size(), getIntProperty("classes.all.amount"));
		
		int instantiationExceptionCounter = 0;
		int illegalAccessExceptionCounter = 0;
		int invokableMethodCounter = 0;
		int stateAnnotatedCounter = 0;
		
		for (Class<?> clazz : classes) {
			try {
				Object object = clazz.newInstance();
				
				if (clazz.isAnnotationPresent(State.class)) {
					stateAnnotatedCounter++;
				}
				
				Method[] methods = clazz.getMethods();
				
				for (Method method : methods) {
					if (method.isAnnotationPresent(TestInvokable.class)) {
						method.invoke(object);
						invokableMethodCounter++;
					}
				}
			} catch (InstantiationException e) {
				instantiationExceptionCounter++;
			} catch (IllegalAccessException e) {
				illegalAccessExceptionCounter++;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		
		assertEquals(instantiationExceptionCounter, getIntProperty("classes.notInstantiable.amount"));
		assertEquals(illegalAccessExceptionCounter, getIntProperty("classes.notAccessible.amount"));
		assertEquals(stateAnnotatedCounter, getIntProperty("classes.stateAnnotated.amount"));
		assertEquals(invokableMethodCounter, getIntProperty("classes.invokableMethod.amount"));
	}
	
	@Test
	public void testGetAnnotatedClasses() {
		Set<Resource> resources = getResources();
		
		for (Resource resource : resources) {
			try {
				if (resource.getAnnotationMetadata(State.class) != null) {
					resource.addClassGenerationListener(listener);
					resource.generateAndDispatchClass();				
				}
			} catch (ConcurrentInspectionException ignored) {
				// can be ignored, because we're not inspecting the resources concurrently
			}
		}
		
		assertEquals(classes.size(), getIntProperty("classes.stateAnnotated.amount"));
	}
	
	private Set<Resource> getResources() {
		Set<ResourceType> resourceTypes = new HashSet<ResourceType>();
		resourceTypes.add(JavaClassResourceType.javaClasses());
		
		Package basePackage = new PackageImpl(getProperty("resources.package"));
		
		return resolver.getResources(resourceTypes, basePackage);
	}
}
