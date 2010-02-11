package net.sf.extcos.resource;

import java.util.Set;

import net.sf.extcos.selector.Package;
import net.sf.extcos.spi.ResourceType;

public interface ResourceResolver {
	Set<Resource> getResources(Set<ResourceType> resourceTypes,
			Package basePackage);
}