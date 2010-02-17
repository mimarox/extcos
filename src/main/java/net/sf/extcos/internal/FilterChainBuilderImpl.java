package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.extcos.collection.MultiplexingSet;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.filter.builder.BuildContext;
import net.sf.extcos.filter.builder.BuildSupport;
import net.sf.extcos.filter.builder.FilterChainBuilder;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class FilterChainBuilderImpl implements FilterChainBuilder {
	// private static Log logger =
	// LogFactory.getLog(FilterChainBuilderImpl.class);

	@Inject
	@Named("fcbi.standardProvider")
	private ResultSetProvider standardProvider;

	@Inject
	private BuildContext buildContext;

	@Inject
	private BuildSupport buildSupport;

	private Set<StoreBinding> storeBindings;
	private StoreReturning returning;
	private Set<Resource> filtered;
	private Set<Class<?>> returnClasses;

	public Filter build(Set<StoreBinding> storeBindings,
			StoreReturning returning, Set<Resource> filtered,
			Set<Class<?>> returnClasses) {
		init(storeBindings, returning, filtered, returnClasses);

		processStoreBindings();
		optimizeFilterObjects();

		finalizeImmediateConnectors();
		return generateRootFilter();
	}

	private void init(Set<StoreBinding> storeBindings,
			StoreReturning returning, Set<Resource> filtered,
			Set<Class<?>> returnClasses) {
		Assert.notNull(returning, iae());
		Assert.notNull(filtered, iae());
		Assert.notNull(returnClasses, iae());

		if (returning instanceof TypeFilterBasedReturning) {
			TypeFilter returningFilter = ((TypeFilterBasedReturning) returning)
					.getTypeFilter();

			StoreBinding returningBinding = new StoreBindingImpl(
					returningFilter, returnClasses);

			storeBindings.add(returningBinding);
		}

		this.storeBindings = storeBindings;
		this.returning = returning;
		this.filtered = filtered;
		this.returnClasses = returnClasses;
	}

	private void processStoreBindings() {
		for (StoreBinding storeBinding : storeBindings) {
			ImmediateConnector ic = new ImmediateConnectorImpl();
			ic.setReceivingSet(storeBinding.getStore());
			buildContext.addImmediateConnector(ic);
			buildSupport.buildFilterObjects(storeBinding.getTypeFilter(), ic);
		}
	}

	private void optimizeFilterObjects() {
		optimizeExtendingFilterObjects();
		optimizeImplementingFilterObjects();
	}

	private void optimizeExtendingFilterObjects() {
		// TODO Auto-generated method stub

	}

	private void optimizeImplementingFilterObjects() {
		// TODO Auto-generated method stub

	}

	private void finalizeImmediateConnectors() {
		Iterable<ImmediateConnector> connectors = buildContext
				.getImmediateConnectors();

		for (ImmediateConnector connector : connectors) {
			connector.setFilteredRegistry(filtered);
		}

		if (returningAllMerged()) {
			for (ImmediateConnector connector : connectors) {
				Set<Class<?>> receivingSet = connector.getReceivingSet();

				MultiplexingSet<Class<?>> multiSet = new BlockingCopyMultiplexingSet<Class<?>>();
				multiSet.setMasterSet(receivingSet);
				multiSet.addSlaveSet(returnClasses);

				connector.setReceivingSet(multiSet);
			}
		}
	}

	private Filter generateRootFilter() {
		Set<FilterObjects> filterObjectss = buildContext.getAllFilterObjects();

		RootFilter rootFilter = new RootFilter();

		if (!filterObjectss.isEmpty()) {
			Set<Filter> filters = new LinkedHashSet<Filter>();

			for (FilterObjects filterObjects : filterObjectss) {
				filters.add(filterObjects.buildFilter());
			}

			rootFilter.setResultSetProvider(standardProvider);
			rootFilter.setChildFilters(filters);
		}

		if (returningAll()) {
			ImmediateConnector connector = new ImmediateConnectorImpl();
			connector.setFilteredRegistry(filtered);
			connector.setReceivingSet(returnClasses);

			MultiplexingConnector dispatcher = new StandardMultiplexingConnector();
			dispatcher.addConnector(connector);

			rootFilter.setResourceDispatcher(dispatcher);
		}

		return buildContext.prependInterceptors(rootFilter);
	}

	private boolean returningAll() {
		if (returning instanceof EnumBasedReturning) {
			return ((EnumBasedReturning) returning).getReturningType() == Returning.ALL;
		} else {
			return false;
		}
	}

	private boolean returningAllMerged() {
		if (returning instanceof EnumBasedReturning) {
			return ((EnumBasedReturning) returning).getReturningType() == Returning.ALL_MERGED;
		} else {
			return false;
		}
	}
}