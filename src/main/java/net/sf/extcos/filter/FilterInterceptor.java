package net.sf.extcos.filter;

public interface FilterInterceptor extends Filter {
	void setInterceptedFilter(Filter filter);
}