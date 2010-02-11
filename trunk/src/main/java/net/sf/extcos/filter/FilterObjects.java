package net.sf.extcos.filter;

import java.util.Set;

public interface FilterObjects {
	void addFilterObjects(FilterObjects filterObjects);
	
	void addAllFilterObjects(Set<FilterObjects> filterObjects);
	
	Filter buildFilter();

	ResourceMatcher getResourceMatcher();

	MultiplexingConnector getResourceDispatcher();

	void setResourceMatcher(ResourceMatcher matcher);

	void setFilter(MatchingChainedFilter filter);

	void setResultSetProvider(ResultSetProvider provider);

	void setResourceDispatcher(MultiplexingConnector dispatcher);
}