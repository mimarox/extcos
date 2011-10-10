package net.sf.extcos.internal;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.resource.Resource;

public class StandardMultiplexingConnector extends
AbstractMultiplexingConnector {

	@Override
	protected void doConnect(final Resource resource) {
		for (Connector connector : connectors) {
			connector.connect(resource);
		}
	}
}