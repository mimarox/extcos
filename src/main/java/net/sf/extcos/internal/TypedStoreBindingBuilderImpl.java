package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypedStoreBindingBuilder;
import net.sf.extcos.util.Assert;

public class TypedStoreBindingBuilderImpl<T> implements
		TypedStoreBindingBuilder<T> {
	
	private TypeFilter filter;
	
	public TypedStoreBindingBuilderImpl(TypeFilter filter) {
		Assert.notNull(filter, IllegalArgumentException.class);
		this.filter = filter;
	}
	
	public StoreBinding into(Set<Class<? extends T>> store) {
		return new StoreBindingImpl(filter, cast(store));
	}
	
	@SuppressWarnings("unchecked")
	private Set<Class<?>> cast(Set<Class<? extends T>> store) {
		return (Set<Class<?>>) (Set<? extends Class<?>>) store;
	}
}