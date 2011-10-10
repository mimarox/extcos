package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMultiplexingConnector implements MultiplexingConnector {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Set<Connector> connectors = new HashSet<Connector>();

	@Override
	public void addConnector(final Connector connector) {
		try {
			Assert.notNull(connector, IllegalArgumentException.class,
					"connector must not be null");

			connectors.add(connector);

			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully added connector ", connector));
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't add connector", e);
		}
	}


	@Override
	public void merge(final Connector connector) {
		addConnector(connector);
	}


	@Override
	public void connect(final Resource resource) {
		if (connectors.isEmpty()) {
			logger.debug("can't connect: no connectors are set");
			return;
		}

		doConnect(resource);

		if (logger.isTraceEnabled()) {
			logger.trace(append("successfully dispatched resource ", resource));
		}
	}

	protected abstract void doConnect(Resource resource);
}
