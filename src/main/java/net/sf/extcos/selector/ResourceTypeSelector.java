package net.sf.extcos.selector;

import java.util.Set;

import net.sf.extcos.spi.ResourceType;

public interface ResourceTypeSelector {
	BasePackageSelector select(ResourceType... resourceTypes);
	
	Set<ResourceType> getResourceTypes();
	
	BasePackageSelector getBasePackageSelector();
}