package net.sf.extcos.filter.builder;

import net.sf.extcos.selector.TypeFilter;

public interface FilterObjectsBuilderFactory {
	FilterObjectsBuilder getFilterObjectsBuilder(TypeFilter typeFilter);
}