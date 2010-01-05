package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.builder.FilterChainBuilder;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.BasePackageSelector;
import net.sf.extcos.selector.ClassSelectionProcessor;
import net.sf.extcos.selector.ClassSelector;
import net.sf.extcos.selector.ForwardingBuilder;
import net.sf.extcos.selector.Package;
import net.sf.extcos.selector.ResourceTypeSelector;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;
import net.sf.extcos.spi.ResourceType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ClassSelectionProcessorImpl implements
		ClassSelectionProcessor {
	@Inject
	private ClassSelector classSelector;

	@Inject
	private ResourceTypeSelector resourceTypeSelector;

	@Inject
	private ResourceResolver resourceResolver;

	@Inject
	private FilterChainBuilder filterChainBuilder;

	@Inject
	@Named("cspi.resources")
	private Set<Resource> resources;
	
	@Inject
	@Named("cspi.filtered")
	private Set<Resource> filtered;
	
	@Inject
	@Named("cspi.classes")
	private Set<Class<?>> classes;
	
	private Set<ResourceType> resourceTypes;

	private Set<Package> basePackages;

	private Set<StoreBinding> storeBindings;

	private StoreReturning returning;

	public Set<Class<?>> process() {
		init();

		for (Package basePackage : basePackages) {
			resources.addAll(resourceResolver.getResources(resourceTypes,
					basePackage));
		}

		if (!resources.isEmpty()) {
			Filter filter = filterChainBuilder.build(storeBindings, returning,
					filtered, classes);

			filter.filter(resources.iterator());

			for (Resource resource : filtered) {
				resource.generateAndDispatchClass();
			}
		}

		return classes;
	}

	private void init() {
		classSelector.configure(resourceTypeSelector);

		resourceTypes = resourceTypeSelector.getResourceTypes();

		BasePackageSelector basePackageSelector = resourceTypeSelector
				.getBasePackageSelector();

		basePackages = basePackageSelector.getBasePackages();

		ForwardingBuilder forwardingBuilder = basePackageSelector
				.getForwardingBuilder();

		storeBindings = forwardingBuilder.getStoreBindings();
		returning = forwardingBuilder.getReturning();
	}
}