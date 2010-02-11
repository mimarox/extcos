package net.sf.extcos.filter;

import java.util.Set;

import net.sf.extcos.resource.Resource;

public interface ResultSetProvider {
	Set<Resource> getResultSet();
}