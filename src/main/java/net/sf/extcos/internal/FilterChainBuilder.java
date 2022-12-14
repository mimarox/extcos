package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.extcos.collection.MultiplexingSet;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class FilterChainBuilder {
	// private static Logger logger =
	// LoggerFactory.getLogger(FilterChainBuilder.class);

	private final ResultSetProvider standardProvider = new StandardResultSetProvider();

	private final BuildContext buildContext = BuildContext.getInstance();
	private final BuildSupport buildSupport = new BuildSupport();

	private Set<StoreBinding> storeBindings;
	private StoreReturning returning;
	private Set<Resource> filtered;
	private Set<Class<?>> returnClasses;

	@SuppressWarnings("hiding")
	public Filter build(final Set<StoreBinding> storeBindings,
			final StoreReturning returning, final Set<Resource> filtered,
			final Set<Class<?>> returnClasses) {
		init(storeBindings, returning, filtered, returnClasses);

		processStoreBindings();
		optimizeFilterObjects();

		finalizeImmediateConnectors();
		return generateRootFilter();
	}

	@SuppressWarnings("hiding")
	private void init(final Set<StoreBinding> storeBindings,
			final StoreReturning returning, final Set<Resource> filtered,
			final Set<Class<?>> returnClasses) {
		Assert.notNull(returning, iae());
		Assert.notNull(filtered, iae());
		Assert.notNull(returnClasses, iae());

		if (returning instanceof TypeFilterBasedReturning) {
			TypeFilter returningFilter = ((TypeFilterBasedReturning) returning)
					.getTypeFilter();

			StoreBinding returningBinding = new StoreBinding(
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

		return rootFilter;
	}

	private boolean returningAll() {
		if (returning instanceof EnumBasedReturning) {
			return ((EnumBasedReturning) returning).getReturningType() == Returning.ALL;
		}

		return false;
	}

	private boolean returningAllMerged() {
		if (returning instanceof EnumBasedReturning) {
			return ((EnumBasedReturning) returning).getReturningType() == Returning.ALL_MERGED;
		}

		return false;
	}
}
