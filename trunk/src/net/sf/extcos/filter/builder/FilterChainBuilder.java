package net.sf.extcos.filter.builder;

import java.util.Set;

import net.sf.extcos.filter.Filter;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;

public interface FilterChainBuilder {
	Filter build(Set<StoreBinding> storeBindings, StoreReturning returning,
			Set<Resource> filtered, Set<Class<?>> returnClasses);
}