package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.util.HashMap;
import java.util.Map;

import net.sf.extcos.resource.Resource;

public class ConjunctiveChainedConnector extends AbstractChainedConnector {
	private final Map<Resource, Integer> waitingResources = new HashMap<Resource, Integer>();

	@Override
	public void connect(final Resource resource) {
		if (connector == null) {
			logger.debug("can't connect: parent connector is not set");
			return;
		}

		if (!waitingResources.keySet().contains(resource)) {
			waitingResources.put(resource, 1);

			if (logger.isTraceEnabled()) {
				logger.trace(append("added resource ", resource, " to waiting resources"));
			}
		} else {
			int count = waitingResources.get(resource) + 1;

			if (count == childCount) {
				connector.connect(resource);

				if (logger.isTraceEnabled()) {
					logger.trace(append("successfully dispatched resource ", resource));
				}
			} else if (count < childCount){
				waitingResources.put(resource, count + 1);

				if (logger.isTraceEnabled()) {
					logger.trace(append("increased the received count for resource ", resource));
				}
			} else {
				logger.debug(append("received resource ", resource, " more often than expected"));
			}
		}
	}
}