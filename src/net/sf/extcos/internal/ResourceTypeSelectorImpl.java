package net.sf.extcos.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.selector.BasePackageSelector;
import net.sf.extcos.selector.ResourceTypeSelector;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;

public class ResourceTypeSelectorImpl implements ResourceTypeSelector {
	private Set<ResourceType> resourceTypes;
	
	@Inject
	private BasePackageSelector resourceSelectionBuilder;
	
	public BasePackageSelector select(ResourceType... resourceTypes) {
		Assert.notEmpty(resourceTypes, IllegalArgumentException.class,
				"there must be at least one resourceType set");
		
		this.resourceTypes = new HashSet<ResourceType>(
				Arrays.asList(resourceTypes));
		
		return resourceSelectionBuilder;
	}

	public BasePackageSelector getBasePackageSelector() {
		return resourceSelectionBuilder;
	}

	public Set<ResourceType> getResourceTypes() {
		return resourceTypes;
	}
}