package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypeFilterDisjunction;
import net.sf.extcos.util.Assert;

public class TypeFilterDisjunctionImpl implements TypeFilterDisjunction {
	private Set<TypeFilter> typeFilters;
	
	public TypeFilterDisjunctionImpl(TypeFilter... filters) {
		Assert.notEmpty(filters, iae());
		
		typeFilters = new HashSet<TypeFilter>();
		
		for (TypeFilter filter : filters) {
			typeFilters.add(filter);
		}
	}
}