package net.sf.extcos.filter;

import java.util.Set;

public interface ChainedFilter extends Filter {
	void setChildFilters(Set<Filter> filters);
	void setResourceDispatcher(MultiplexingConnector resourceDispatcher);
	void setResultSetProvider(ResultSetProvider resultSetProvider);
}