package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.Set;

import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.MatchingChainedFilter;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.filter.builder.BuildContext;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class FilterObjectsImpl implements FilterObjects {
	
	@Inject
	private BuildContext buildContext;
	
	@Inject
	@Named("foi.children")
	private Set<FilterObjects> children;
	
	@Inject
	@Named("foi.filters")
	private Set<Filter> filters;
	
	private MatchingChainedFilter filter;
	private MultiplexingConnector dispatcher;
	private ResourceMatcher matcher;
	private ResultSetProvider provider;
	
	public void addFilterObjects(FilterObjects filterObjects) {
		Assert.notNull(filterObjects, iae());
		children.add(filterObjects);
	}
	
	public void addAllFilterObjects(Set<FilterObjects> filterObjects) {
		Assert.notEmpty(filterObjects, iae());
		children.addAll(filterObjects);
	}

	public Filter buildFilter() {
		filter.setResourceDispatcher(dispatcher);
		filter.setResourceMatcher(matcher);
		filter.setResultSetProvider(provider);
		
		filters.clear();
		for (FilterObjects filterObjects : children) {
			filters.add(filterObjects.buildFilter());
		}
		
		filter.setChildFilters(filters);
		return buildContext.prependInterceptors(filter);
	}

	public MultiplexingConnector getResourceDispatcher() {
		return dispatcher;
	}

	public ResourceMatcher getResourceMatcher() {
		return matcher;
	}

	public void setFilter(MatchingChainedFilter filter) {
		Assert.notNull(filter, iae());
		this.filter = filter;
	}

	public void setResourceDispatcher(MultiplexingConnector dispatcher) {
		Assert.notNull(dispatcher, iae());
		this.dispatcher = dispatcher;
	}

	public void setResourceMatcher(ResourceMatcher matcher) {
		Assert.notNull(matcher, iae());
		this.matcher = matcher;
	}

	public void setResultSetProvider(ResultSetProvider provider) {
		Assert.notNull(provider, iae());
		this.provider = provider;
	}
}