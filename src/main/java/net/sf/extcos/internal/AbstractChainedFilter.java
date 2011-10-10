package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.util.Iterator;
import java.util.Set;

import net.sf.extcos.filter.ChainedFilter;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractChainedFilter implements ChainedFilter {
	protected Logger logger = LoggerFactory.getLogger(getClass());

	private Set<Filter> filters;

	private MultiplexingConnector resourceDispatcher;

	private ResultSetProvider resultSetProvider;

	@Override
	public void setChildFilters(final Set<Filter> filters) {
		try {
			Assert.notEmpty(filters, IllegalArgumentException.class,
					"filters must not be null or empty");

			this.filters = filters;

			if (logger.isTraceEnabled()) {
				logger.trace("successfully set filters");
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set filters", e);
		}
	}

	@Override
	public void setResourceDispatcher(final MultiplexingConnector resourceDispatcher) {
		try {
			Assert.notNull(resourceDispatcher, IllegalArgumentException.class,
					"resourceDispatcher must not be null");

			this.resourceDispatcher = resourceDispatcher;

			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully set resource dispatcher to ", resourceDispatcher));
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set resource dispatcher", e);
		}
	}

	@Override
	public void setResultSetProvider(final ResultSetProvider resultSetProvider) {
		try {
			Assert.notNull(resultSetProvider, IllegalArgumentException.class,
					"resultSetProvider must not be null");

			this.resultSetProvider = resultSetProvider;

			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully set result set provider to ", resultSetProvider));
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set result set provider", e);
		}
	}

	@Override
	public final void filter(final Iterable<Resource> resources) {
		if (resources == null) {
			logger.debug("resources must not be null, nothing to filter");
			return;
		}

		if (resourceDispatcher == null && filters == null) {
			logger.warn("no resource dispatcher or filters are set, hence no filtering will take place");
			return;
		} else if (resourceDispatcher == null && filters != null) {
			if (resultSetProvider == null) {
				invokeFilters(resources);
			} else {
				invokeFilters(
						filter(resources.iterator(),
								resultSetProvider.getResultSet()));
			}
		} else if (resourceDispatcher != null && filters == null) {
			filter(resources.iterator(), resourceDispatcher);
		} else /* resourceDispatcher != null && filters != null */{
			if (resultSetProvider == null) {
				filter(resources.iterator(), resourceDispatcher);
				invokeFilters(resources);
			} else {
				invokeFilters(
						filter(resources.iterator(), resourceDispatcher,
								resultSetProvider.getResultSet()));
			}
		}
	}

	private void invokeFilters(final Iterable<Resource> resources) {
		for (Filter filter : filters) {
			filter.filter(resources);
		}
	}

	protected abstract Iterable<Resource> filter(Iterator<Resource> resources, Set<Resource> resultSet);

	@SuppressWarnings("hiding")
	protected abstract void filter(Iterator<Resource> resources, MultiplexingConnector resourceDispatcher);

	@SuppressWarnings("hiding")
	protected abstract Iterable<Resource> filter(Iterator<Resource> resources, MultiplexingConnector resourceDispatcher, Set<Resource> resultSet);
}
