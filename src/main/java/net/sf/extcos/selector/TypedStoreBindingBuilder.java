package net.sf.extcos.selector;

import java.util.Set;

public interface TypedStoreBindingBuilder<T> {
	StoreBinding into(Set<Class<? extends T>> store);
}