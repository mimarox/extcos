package net.sf.extcos.internal;

import net.sf.extcos.selector.DirectReturning;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class TypeFilterBasedReturning implements DirectReturning {
	private TypeFilter typeFilter;
	
	public TypeFilterBasedReturning(TypeFilter typeFilter) {
		Assert.notNull(typeFilter, IllegalArgumentException.class);
		this.typeFilter = typeFilter;
	}
	
	public TypeFilter getTypeFilter() {
		return typeFilter;
	}
}