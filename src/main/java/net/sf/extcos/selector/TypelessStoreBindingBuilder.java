package net.sf.extcos.selector;

import java.util.Set;

public interface TypelessStoreBindingBuilder {
	StoreBinding into(Set<Class<?>> store);
}