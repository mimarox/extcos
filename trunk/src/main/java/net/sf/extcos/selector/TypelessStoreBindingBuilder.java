package net.sf.extcos.selector;

import java.util.Set;

import net.sf.extcos.util.Assert;

public class TypelessStoreBindingBuilder {
	private final TypeFilter filter;

	public TypelessStoreBindingBuilder(final TypeFilter filter) {
		Assert.notNull(filter, IllegalArgumentException.class);
		this.filter = filter;
	}

	public StoreBinding into(final Set<Class<?>> store) {
		return new StoreBinding(filter, store);
	}
}