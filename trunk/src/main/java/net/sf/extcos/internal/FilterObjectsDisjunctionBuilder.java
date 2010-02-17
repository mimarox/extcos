package net.sf.extcos.internal;

import net.sf.extcos.filter.ChainedConnector;

public class FilterObjectsDisjunctionBuilder extends
		AbstractFilterObjectsJunctionBuilder {

	protected ChainedConnector getConnector() {
		return injector.getInstance(DisjunctiveChainedConnector.class);
	}
}