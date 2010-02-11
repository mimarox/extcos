package net.sf.extcos.internal;

import net.sf.extcos.filter.BlacklistManager;
import net.sf.extcos.filter.Connector;
import net.sf.extcos.resource.Resource;

import com.google.inject.Inject;

public class BlacklistAwareMultiplexingConnector extends
		AbstractMultiplexingConnector {

	@Inject
	private BlacklistManager blacklistManager;
	
	protected void doConnect(Resource resource) {
		blacklistManager.blacklist(resource);
		
		for (Connector connector : connectors) {
			connector.connect(resource);
		}
	}
}