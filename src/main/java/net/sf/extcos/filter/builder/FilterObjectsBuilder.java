package net.sf.extcos.filter.builder;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.selector.TypeFilter;

public interface FilterObjectsBuilder {
	void buildFilterObjects(TypeFilter filter, Connector connector);
}