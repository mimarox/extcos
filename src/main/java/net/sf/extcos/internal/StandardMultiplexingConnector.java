package net.sf.extcos.internal;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.resource.Resource;

public class StandardMultiplexingConnector extends
		AbstractMultiplexingConnector {

	protected void doConnect(Resource resource) {
		for (Connector connector : connectors) {
			connector.connect(resource);
		}
	}
}