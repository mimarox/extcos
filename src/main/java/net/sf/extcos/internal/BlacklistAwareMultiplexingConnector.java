package net.sf.extcos.internal;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.resource.Resource;

public class BlacklistAwareMultiplexingConnector extends
AbstractMultiplexingConnector {

	private final BlacklistManager blacklistManager = BlacklistManager.getInstance();

	@Override
	protected void doConnect(final Resource resource) {
		blacklistManager.blacklist(resource);

		for (Connector connector : connectors) {
			connector.connect(resource);
		}
	}
}