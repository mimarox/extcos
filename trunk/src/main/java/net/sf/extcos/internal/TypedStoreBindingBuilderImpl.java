package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypedStoreBindingBuilder;
import net.sf.extcos.util.Assert;

public class TypedStoreBindingBuilderImpl<T> implements
TypedStoreBindingBuilder<T> {

	private final TypeFilter filter;

	public TypedStoreBindingBuilderImpl(final TypeFilter filter) {
		Assert.notNull(filter, IllegalArgumentException.class);
		this.filter = filter;
	}

	@Override
	public StoreBinding into(final Set<Class<? extends T>> store) {
		return new StoreBindingImpl(filter, cast(store));
	}

	@SuppressWarnings("unchecked")
	private Set<Class<?>> cast(final Set<Class<? extends T>> store) {
		return (Set<Class<?>>) (Set<? extends Class<?>>) store;
	}
}