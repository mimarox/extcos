package net.sf.extcos.internal;

import net.sf.extcos.filter.ChainedConnector;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class FilterObjectsConjunctionBuilder extends
		AbstractFilterObjectsJunctionBuilder {
	
	@Inject
	private Injector injector;
	
	protected ChainedConnector getConnector() {
		return injector.getInstance(ConjunctiveChainedConnector.class);
	}
}