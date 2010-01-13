package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.HashSet;

import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.MultipleTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class TypeFilterConjunction extends AbstractTypeFilterJunction {
	public TypeFilterConjunction(MultipleTypeFilter... filters) {
		Assert.notEmpty(filters, iae());
		init(filters);
	}
	
	public TypeFilterConjunction(ExtendingTypeFilter filter,
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
}