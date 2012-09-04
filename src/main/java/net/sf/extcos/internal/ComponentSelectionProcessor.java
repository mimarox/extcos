package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.Set;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.BasePackageSelector;
import net.sf.extcos.selector.ForwardingBuilder;
import net.sf.extcos.selector.Package;
import net.sf.extcos.selector.ResourceTypeSelector;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;
import net.sf.extcos.spi.QueryContext;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.Assert;

public class ComponentSelectionProcessor {
	private final ComponentQuery componentSelector;

	private final ResourceTypeSelector resourceTypeSelector = new ResourceTypeSelector();
	private final ResourceResolver resourceResolver = new ResourceResolver();
	private final FilterChainBuilder filterChainBuilder = new FilterChainBuilder();

	private final Set<Resource> resources = new ArraySet<Resource>();
	private final Set<Resource> filtered = new ArraySet<Resource>();
	private final Set<Class<?>> classes = new ArraySet<Class<?>>();

	private Set<ResourceType> resourceTypes;
	private Set<Package> basePackages;
	private Set<StoreBinding> storeBindings;
	private StoreReturning returning;

	public ComponentSelectionProcessor(final ComponentQuery componentSelector) {
		Assert.notNull(componentSelector, iae());
		this.componentSelector = componentSelector;
	}

	public Set<Class<?>> process() {
		init();

		for (Package basePackage : basePackages) {
			resources.addAll(resourceResolver.getResources(resourceTypes,
					basePackage));
		}

		if (!resources.isEmpty()) {
			Filter filter = filterChainBuilder.build(storeBindings, returning,
					filtered, classes);

			filter.filter(resources);

			for (Resource resource : filtered) {
				resource.generateAndDispatchClass();
			}
		}

		BuildContext.getInstance().reset();
		QueryContext.getInstance().reset();
		
		return classes;
	}

	private void init() {
		componentSelector.configure(resourceTypeSelector);

		resourceTypes = resourceTypeSelector.getResourceTypes();

		BasePackageSelector basePackageSelector = resourceTypeSelector
				.getBasePackageSelector();

		basePackages = basePackageSelector.getBasePackages();
		QueryContext.getInstance().setIncludeEnums(basePackageSelector.isIncludingEnums());
		
		ForwardingBuilder forwardingBuilder = basePackageSelector
				.getForwardingBuilder();

		storeBindings = forwardingBuilder.getStoreBindings();
		returning = forwardingBuilder.getReturning();
	}
}