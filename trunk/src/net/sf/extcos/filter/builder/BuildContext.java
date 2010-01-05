package net.sf.extcos.filter.builder;

import java.util.Set;

import net.sf.extcos.filter.ChainedFilter;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.MergableConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.selector.TypeFilter;

public interface BuildContext {
	boolean isRegistered(TypeFilter typeFilter);
	MergableConnector getConnector(TypeFilter typeFilter);
	void register(TypeFilter typeFilter, MergableConnector connector);
	
	boolean isRegistered(ResourceMatcher resourceMatcher);
	FilterObjects getFilterObjects(ResourceMatcher resourceMatcher);
	void register(FilterObjects filterObjects);
	
	void addImmediateConnector(ImmediateConnector connector);
	Iterable<ImmediateConnector> getImmediateConnectors();
	
	Set<FilterObjects> getAllFilterObjects();
	Set<FilterObjects> getExtendingFilterObjects();
	Set<FilterObjects> getImplementingFilterObjects();
	
	Filter prependInterceptors(ChainedFilter filter);
}