package net.sf.extcos.selector;

import java.util.Set;

public interface StoreBinding {
	TypeFilter getTypeFilter();
	Set<Class<?>> getStore();
}