package net.sf.extcos.filter;

import net.sf.extcos.resource.Resource;

public interface Filter {
	void filter(Iterable<Resource> resources);
}