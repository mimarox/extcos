package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.util.Set;

import net.sf.extcos.resource.Resource;

public class DisjunctiveChainedConnector extends AbstractChainedConnector {
	private final Set<Resource> receivedResources = new ArraySet<Resource>();

	@Override
	public synchronized void connect(final Resource resource) {
		if (connector == null) {
			logger.debug("can't connect: parent connector is not set");
			return;
		}

		if (!receivedResources.contains(resource)) {
			receivedResources.add(resource);
			connector.connect(resource);

			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully dispatched resource ", resource));
			}
		} else {
			if (logger.isTraceEnabled()) {
				logger.trace(append("stopped previously dispatched resource ", resource));
			}
		}
	}
}