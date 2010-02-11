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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractChainedFilter implements ChainedFilter {
	protected Log logger = LogFactory.getLog(getClass());
	
	private Set<Filter> filters;
	
	private MultiplexingConnector resourceDispatcher;
	
	private ResultSetProvider resultSetProvider;
	
	public void setChildFilters(Set<Filter> filters) {
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

	public void setResourceDispatcher(MultiplexingConnector resourceDispatcher) {
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

	public void setResultSetProvider(ResultSetProvider resultSetProvider) {
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
	
	public final void filter(Iterator<Resource> resources) {
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
						filter(resources,
								resultSetProvider.getResultSet()));
			}
		} else if (resourceDispatcher != null && filters == null) {
			filter(resources, resourceDispatcher);
		} else /* resourceDispatcher != null && filters != null */{
			if (resultSetProvider == null) {
				filter(resources, resourceDispatcher);
				invokeFilters(resources);				
			} else {
				invokeFilters(
						filter(resources, resourceDispatcher,
								resultSetProvider.getResultSet()));
			}
		}
	}

	private void invokeFilters(Iterator<Resource> resources) {
		for (Filter filter : filters) {
			filter.filter(resources);
		}
	}
	
	protected abstract Iterator<Resource> filter(Iterator<Resource> resources, Set<Resource> resultSet);
	
	protected abstract void filter(Iterator<Resource> resources, MultiplexingConnector resourceDispatcher);
	
	protected abstract Iterator<Resource> filter(Iterator<Resource> resources, MultiplexingConnector resourceDispatcher, Set<Resource> resultSet);
}