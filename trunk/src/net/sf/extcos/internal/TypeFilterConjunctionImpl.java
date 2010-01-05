package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.MultipleTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypeFilterConjunction;
import net.sf.extcos.util.Assert;

public class TypeFilterConjunctionImpl implements TypeFilterConjunction {
	private Set<TypeFilter> typeFilters;
	
	public TypeFilterConjunctionImpl(MultipleTypeFilter... filters) {
		Assert.notEmpty(filters, iae());
		init(filters);
	}
	
	public TypeFilterConjunctionImpl(ExtendingTypeFilter filter,
			MultipleTypeFilter... filters) {
		Assert.notNull(filter, iae());
		Assert.notNull(filters, iae());
		
		init(filters);
		typeFilters.add(filter);
	}
	
	private void init(MultipleTypeFilter[] filters) {
		typeFilters = new HashSet<TypeFilter>();
		
		for (TypeFilter filter : filters) {
			typeFilters.add(filter);
		}
	}

	public Set<TypeFilter> getTypeFilters() {
		return typeFilters;
	}
}