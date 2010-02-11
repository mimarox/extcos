package net.sf.extcos.selector;

import java.util.Set;

public interface TypeFilterJunction extends MultipleTypeFilter {
	Set<TypeFilter> getTypeFilters();
}