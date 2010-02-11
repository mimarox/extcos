package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.StringUtils.append;

import java.util.Iterator;

import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.FilterInterceptor;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;

public class ThreadingFilterInterceptor implements FilterInterceptor {
	private static Log logger = LogFactory.getLog(ThreadingFilterInterceptor.class);
	
	private Filter filter;
	
	private ThreadManager threadManager;
	
	@Inject
	public ThreadingFilterInterceptor(ThreadManager threadManager) {
		Assert.notNull(threadManager, iae());
		threadManager.register();
		this.threadManager = threadManager;
	}
	
	public void setInterceptedFilter(Filter filter) {
		try {
			Assert.notNull(filter, IllegalArgumentException.class,
					"filter must not be null");
			
			this.filter = filter;
			
			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully set intercepted filter to ", filter));
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set intercepted filter", e);
		}
	}

	public void filter(final Iterator<Resource> resources) {
		if (filter == null) {
			logger.warn("intercepted filter is not set, hence nothing to intercept");
			return;
		}
		
		threadManager.invoke(new Runnable() {
			public void run() {
				filter.filter(resources);
			}
		});
	}
}