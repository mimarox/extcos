package net.sf.extcos.internal;

import com.google.inject.Inject;
import com.google.inject.Injector;

import net.sf.extcos.filter.ChainedConnector;

public class FilterObjectsDisjunctionBuilder extends
		AbstractFilterObjectsJunctionBuilder {

	@Inject
	private Injector injector;
	
	protected ChainedConnector getConnector() {
		return injector.getInstance(DisjunctiveChainedConnector.class);
	}
}