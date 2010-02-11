package net.sf.extcos.filter;

import java.util.Iterator;

import net.sf.extcos.resource.Resource;

public interface Filter {
	void filter(Iterator<Resource> resources);
}