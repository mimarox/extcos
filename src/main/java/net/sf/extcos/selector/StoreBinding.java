package net.sf.extcos.selector;

import java.util.Set;

import net.sf.extcos.util.Assert;

public class StoreBinding {
	private final TypeFilter filter;
	private final Set<Class<?>> store;

	public StoreBinding(final TypeFilter filter, final Set<Class<?>> store) {
		Assert.notNull(filter, IllegalArgumentException.class);
		Assert.notNull(store, IllegalArgumentException.class);

		this.filter = filter;
		this.store = store;
		this.store.clear();
	}

	public Set<Class<?>> getStore() {
		return store;
	}

	public TypeFilter getTypeFilter() {
		return filter;
	}
}