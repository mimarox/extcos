package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.Set;

import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.MatchingChainedFilter;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.util.Assert;

public class FilterObjects {
	private final BuildContext buildContext = BuildContext.getInstance();
	private final Set<FilterObjects> children = new ArraySet<FilterObjects>();
	private final Set<Filter> filters = new ArraySet<Filter>();

	private MatchingChainedFilter filter;
	private MultiplexingConnector dispatcher;
	private ResourceMatcher matcher;
	private ResultSetProvider provider;

	public void addFilterObjects(final FilterObjects filterObjects) {
		Assert.notNull(filterObjects, iae());
		children.add(filterObjects);
	}

	public void addAllFilterObjects(final Set<FilterObjects> filterObjects) {
		Assert.notEmpty(filterObjects, iae());
		children.addAll(filterObjects);
	}

	public Filter buildFilter() {
		filter.setResourceDispatcher(dispatcher);
		filter.setResourceMatcher(matcher);

		if (!children.isEmpty()) {
			filters.clear();
			for (FilterObjects filterObjects : children) {
				filters.add(filterObjects.buildFilter());
			}

			filter.setChildFilters(filters);
			filter.setResultSetProvider(provider);
		}

		return buildContext.prependInterceptors(filter);
	}

	public MultiplexingConnector getResourceDispatcher() {
		return dispatcher;
	}

	public ResourceMatcher getResourceMatcher() {
		return matcher;
	}

	public void setFilter(final MatchingChainedFilter filter) {
		Assert.notNull(filter, iae());
		this.filter = filter;
	}

	public void setResourceDispatcher(final MultiplexingConnector dispatcher) {
		Assert.notNull(dispatcher, iae());
		this.dispatcher = dispatcher;
	}

	public void setResourceMatcher(final ResourceMatcher matcher) {
		Assert.notNull(matcher, iae());
		this.matcher = matcher;
	}

	public void setResultSetProvider(final ResultSetProvider provider) {
		Assert.notNull(provider, iae());
		this.provider = provider;
	}
}