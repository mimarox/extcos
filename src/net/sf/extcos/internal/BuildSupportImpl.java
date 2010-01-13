package net.sf.extcos.internal;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.builder.BuildContext;
import net.sf.extcos.filter.builder.BuildSupport;
import net.sf.extcos.filter.builder.FilterObjectsBuilderFactory;
import net.sf.extcos.selector.TypeFilter;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BuildSupportImpl implements BuildSupport {
	
	@Inject
	private BuildContext buildContext;
	
	@Inject
	private FilterObjectsBuilderFactory factory;
	
	public void buildFilterObjects(TypeFilter filter, Connector connector) {
		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
			
			if (connector instanceof ImmediateConnector) {
				buildContext.addImmediateConnector((ImmediateConnector) connector);
			}
		} else {
			factory.getFilterObjectsBuilder(filter).buildFilterObjects(filter, connector);
		}
	}
}