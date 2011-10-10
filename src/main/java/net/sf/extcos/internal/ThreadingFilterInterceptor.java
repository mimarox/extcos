package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.StringUtils.append;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.FilterInterceptor;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class ThreadingFilterInterceptor implements FilterInterceptor {
	private static Logger logger = LoggerFactory.getLogger(ThreadingFilterInterceptor.class);

	private Filter filter;

	private final ThreadManager threadManager;

	@Inject
	public ThreadingFilterInterceptor(final ThreadManager threadManager) {
		Assert.notNull(threadManager, iae());
		threadManager.register();
		this.threadManager = threadManager;
	}

	@Override
	public void setInterceptedFilter(final Filter filter) {
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

	@Override
	public void filter(final Iterable<Resource> resources) {
		if (filter == null) {
			logger.warn("intercepted filter is not set, hence nothing to intercept");
			return;
		}

		threadManager.invoke(new Runnable() {
			@Override
			public void run() {
				filter.filter(resources);
			}
		});
	}
}
