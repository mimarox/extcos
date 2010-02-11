package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class StoreBindingImpl implements StoreBinding {
	private TypeFilter filter;
	private Set<Class<?>> store;
	
	public StoreBindingImpl(TypeFilter filter, Set<Class<?>> store) {
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