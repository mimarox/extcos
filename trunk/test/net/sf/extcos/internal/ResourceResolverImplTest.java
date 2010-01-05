package net.sf.extcos.internal;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.internal.JavaClassResourceType;
import net.sf.extcos.internal.PackageImpl;
import net.sf.extcos.internal.ResourceResolverImpl;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.Package;
import net.sf.extcos.spi.ResourceType;

import org.testng.annotations.Test;

public class ResourceResolverImplTest {
	@Test
	public void testGetResources() {
		Set<ResourceType> resourceTypes = new HashSet<ResourceType>();
		resourceTypes.add(JavaClassResourceType.javaClasses());
		
		Package basePackage = new PackageImpl("com");
		
		ResourceResolver resolver = new ResourceResolverImpl();
		resolver.getResources(resourceTypes, basePackage);
	}
}