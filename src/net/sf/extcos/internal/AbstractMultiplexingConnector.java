package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractMultiplexingConnector implements MultiplexingConnector {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	protected Set<Connector> connectors = new HashSet<Connector>();
	
	public void addConnector(Connector connector) {
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

	
	public void merge(Connector connector) {
		addConnector(connector);
	}
	
	
	public void connect(Resource resource) {
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