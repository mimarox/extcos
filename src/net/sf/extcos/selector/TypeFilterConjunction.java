package net.sf.extcos.selector;

import java.util.Set;

public interface TypeFilterConjunction extends MultipleTypeFilter {
	Set<TypeFilter> getTypeFilters();
}