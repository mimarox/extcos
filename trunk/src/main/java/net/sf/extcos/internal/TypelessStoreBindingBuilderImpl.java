package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypelessStoreBindingBuilder;
import net.sf.extcos.util.Assert;

public class TypelessStoreBindingBuilderImpl implements
TypelessStoreBindingBuilder {

	private final TypeFilter filter;

	public TypelessStoreBindingBuilderImpl(final TypeFilter filter) {
		Assert.notNull(filter, IllegalArgumentException.class);
		this.filter = filter;
	}

	@Override
	public StoreBinding into(final Set<Class<?>> store) {
		return new StoreBindingImpl(filter, store);
	}
}