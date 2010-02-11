package net.sf.extcos.internal;

import static org.testng.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.classgeneration.ClassGenerationListener;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.Package;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.PropertyInjector;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ResourceResolverImplTest {
	private ResourceResolver resolver;
	
	@BeforeClass
	public void initResolver() throws NoSuchFieldException {
		resolver = new ResourceResolverImpl();
		PropertyInjector injector = new PropertyInjector();
		injector.setTarget(resolver);
		injector.inject("classLoader", Thread.currentThread().getContextClassLoader());
	}
	
	
	@Test
	public void testGetResources() {
		Set<ResourceType> resourceTypes = new HashSet<ResourceType>();
		resourceTypes.add(JavaClassResourceType.javaClasses());
		
		Package basePackage = new PackageImpl("com.matthiasrothe");
		
		Set<Resource> resources = resolver.getResources(resourceTypes, basePackage);
		
		assertEquals(resources.size(), 2);
		
		final Set<Class<?>> classes = new ArraySet<Class<?>>();
		
		ClassGenerationListener listener = new ClassGenerationListener() {
			public <T> void classGenerated(Class<? extends T> clazz) {
				classes.add(clazz);
			}
		};
		
		for (Resource resource : resources) {
			resource.addClassGenerationListener(listener);
			resource.generateAndDispatchClass();
		}
		
		assertEquals(classes.size(), 2);
	}
}