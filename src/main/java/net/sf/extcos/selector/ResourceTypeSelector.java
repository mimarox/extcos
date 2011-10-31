package net.sf.extcos.selector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.Assert;

public class ResourceTypeSelector {
	private final BasePackageSelector basePackageSelector = new BasePackageSelector();

	private Set<ResourceType> resourceTypes;

	public BasePackageSelector select(@SuppressWarnings("hiding") final ResourceType... resourceTypes) {
		Assert.notEmpty(resourceTypes, IllegalArgumentException.class,
				"there must be at least one resourceType set");

		this.resourceTypes = new HashSet<ResourceType>(
				Arrays.asList(resourceTypes));

		return basePackageSelector;
	}

	public BasePackageSelector getBasePackageSelector() {
		return basePackageSelector;
	}

	public Set<ResourceType> getResourceTypes() {
		return resourceTypes;
	}
}