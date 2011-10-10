package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.StringUtils.append;
import net.sf.extcos.filter.ChainedConnector;
import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChainedConnector implements ChainedConnector {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected Connector connector;

	protected int childCount;

	@Override
	public void setChildCount(final int childCount) {
		try {
			Assert.greaterThan(childCount, 1, iae(),
					append("childCount must at least be 2, but was ", childCount));

			this.childCount = childCount;

			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully set childCount to ", childCount));
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set childCount", e);
		}
	}

	@Override
	public void setParentConnector(final Connector connector) {
		try {
			Assert.notNull(connector, iae(), "connector must not be null");

			this.connector = connector;

			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully set parent connector to ", connector));
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set parent connector", e);
		}
	}


	@Override
	public Connector getParentConnector() {
		return connector;
	}


	@Override
	@SuppressWarnings("hiding")
	public void merge(final Connector connector) {
		try {
			Assert.notNull(connector, iae(), "connector must not be null");

			if (this.connector instanceof MultiplexingConnector) {
				((MultiplexingConnector) this.connector).merge(connector);
			} else {
				MultiplexingConnector tmp = new StandardMultiplexingConnector();
				tmp.addConnector(this.connector);
				tmp.addConnector(connector);
				this.connector = tmp;
			}

			if (logger.isTraceEnabled()) {
				logger.trace("successfully merged parent connectors");
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't merge parent connectors", e);
		}
	}
}
