package net.sf.extcos.filter.builder;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.selector.TypeFilter;

public interface BuildSupport {
	void buildFilterObjects(TypeFilter typeFilter, Connector connector);
}