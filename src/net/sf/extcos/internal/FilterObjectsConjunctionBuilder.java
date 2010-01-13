package net.sf.extcos.internal;

import net.sf.extcos.filter.ChainedConnector;

public class FilterObjectsConjunctionBuilder extends
		AbstractFilterObjectsJunctionBuilder {
	
	protected ChainedConnector getConnector() {
		return injector.getInstance(ConjunctiveChainedConnector.class);
	}
}