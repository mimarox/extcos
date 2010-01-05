package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypelessStoreBindingBuilder;
import net.sf.extcos.util.Assert;

public class TypelessStoreBindingBuilderImpl implements
		TypelessStoreBindingBuilder {
	
	private TypeFilter filter;
	
	public TypelessStoreBindingBuilderImpl(TypeFilter filter) {
		Assert.notNull(filter, IllegalArgumentException.class);
		this.filter = filter;
	}
	
	public StoreBinding into(Set<Class<?>> store) {
		return new StoreBindingImpl(filter, store);
	}
}