package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class StoreBindingImpl implements StoreBinding {
	private final TypeFilter filter;
	private final Set<Class<?>> store;

	public StoreBindingImpl(final TypeFilter filter, final Set<Class<?>> store) {
		Assert.notNull(filter, IllegalArgumentException.class);
		Assert.notNull(store, IllegalArgumentException.class);

		this.filter = filter;
		this.store = store;
		this.store.clear();
	}

	@Override
	public Set<Class<?>> getStore() {
		return store;
	}

	@Override
	public TypeFilter getTypeFilter() {
		return filter;
	}
}