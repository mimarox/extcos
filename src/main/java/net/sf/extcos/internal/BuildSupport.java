package net.sf.extcos.internal;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.internal.factory.FilterObjectsBuilderFactory;
import net.sf.extcos.selector.TypeFilter;

public class BuildSupport {
	private static final BuildContext buildContext = BuildContext.getInstance();
	private static final FilterObjectsBuilderFactory factory = FilterObjectsBuilderFactory.getInstance();

	public void buildFilterObjects(final TypeFilter filter, final Connector connector) {
		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
		} else {
			factory.getFilterObjectsBuilder(filter).buildFilterObjects(filter, connector);
		}
	}
}