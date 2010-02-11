package net.sf.extcos.filter;

import java.util.Set;

import net.sf.extcos.resource.Resource;

public interface ImmediateConnector extends Connector {
	Set<Class<?>> getReceivingSet();
	void setReceivingSet(Set<Class<?>> receivingSet);
	void setFilteredRegistry(Set<Resource> filtered);
}