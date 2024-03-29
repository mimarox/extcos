package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import net.sf.extcos.collection.RandomPollingSet;
import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.exception.StateChangedException;
import net.sf.extcos.filter.MatchingChainedFilter;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

public class MatchingChainedFilterImpl extends AbstractChainedFilter implements MatchingChainedFilter {
	private final RandomPollingSet<Resource> waitingResources = new RandomPollingArraySet<Resource>();
	private Iterator<Resource> resources;
	private ResourceMatcher resourceMatcher;

	@Override
	public void setResourceMatcher(final ResourceMatcher resourceMatcher) {
		try {
			Assert.notNull(resourceMatcher, IllegalArgumentException.class,
					"resourceMatcher must not be null");

			this.resourceMatcher = resourceMatcher;

			if (logger.isTraceEnabled()) {
				logger.trace(append("successfully set resource matcher to ", resourceMatcher));
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set resource matcher", e);
		}
	}

	//filter is node in filter tree, but not an endpoint
	//these filters will be built for performance reasons only
	@Override
	protected Iterable<Resource> filter(@SuppressWarnings("hiding") final Iterator<Resource> resources,
			final Set<Resource> resultSet) {
		this.resources = resources;

		if (resourceMatcher == null) {
			logger.warn("resource matcher is not set, hence no filtering will take place");

			while (hasNextResource()) {
				try {
					resultSet.add(nextResource());
				} catch (StateChangedException e) {
					continue;
				}
			}

			return resultSet;
		}

		while (hasNextResource()) {
			Resource resource = null;

			try {
				resource = nextResource();
			} catch (StateChangedException e) {
				continue;
			}

			try {
				if (resourceMatcher.matches(resource)) {
					resultSet.add(resource);
				}
			} catch (ConcurrentInspectionException e) {
				if (logger.isTraceEnabled()) {
					logger.trace("concurrent resource access", e);
				}

				waitingResources.add(resource);
			}
		}

		return resultSet;
	}

	//filter is leaf in filter tree and must therefore be an endpoint
	@Override
	protected void filter(@SuppressWarnings("hiding") final Iterator<Resource> resources,
			final MultiplexingConnector resourceDispatcher) {
		this.resources = resources;

		if (resourceMatcher == null) {
			logger.warn("resource matcher is not set, hence no filtering will take place");

			while (hasNextResource()) {
				Resource resource = null;

				try {
					resource = nextResource();
				} catch (StateChangedException e) {
					continue;
				}

				resourceDispatcher.connect(resource);
			}
		} else {
			while (hasNextResource()) {
				Resource resource = null;

				try {
					resource = nextResource();
				} catch (StateChangedException e) {
					continue;
				}

				try {
					if (resourceMatcher.matches(resource)) {
						resourceDispatcher.connect(resource);
					}
				} catch (ConcurrentInspectionException e) {
					if (logger.isTraceEnabled()) {
						logger.trace("concurrent resource access", e);
					}

					waitingResources.add(resource);
				}
			}
		}
	}

	//filter is node in filter tree and an endpoint
	@Override
	protected Iterable<Resource> filter(@SuppressWarnings("hiding") final Iterator<Resource> resources,
			final MultiplexingConnector resourceDispatcher, final Set<Resource> resultSet) {
		this.resources = resources;

		if (resourceMatcher == null) {
			logger.warn("resource matcher is not set, hence no filtering will take place");

			while (hasNextResource()) {
				Resource resource = null;

				try {
					resource = nextResource();
				} catch (StateChangedException e) {
					continue;
				}

				resourceDispatcher.connect(resource);
				resultSet.add(resource);
			}

			return resultSet;
		}

		while (hasNextResource()) {
			Resource resource = null;

			try {
				resource = nextResource();
			} catch (StateChangedException e) {
				continue;
			}

			try {
				if (resourceMatcher.matches(resource)) {
					resourceDispatcher.connect(resource);
					resultSet.add(resource);
				}
			} catch (ConcurrentInspectionException e) {
				if (logger.isTraceEnabled()) {
					logger.trace("concurrent resource access", e);
				}

				waitingResources.add(resource);
			}
		}

		return resultSet;
	}

	private boolean hasNextResource() {
		return resources.hasNext() || waitingResources.size() > 0;
	}

	private Resource nextResource() {
		if (resources.hasNext() && waitingResources.size() > 0) {
			Random prng = new Random();

			if (prng.nextBoolean()) {
				return resources.next();
			}

			return waitingResources.pollRandom();
		} else if (resources.hasNext()) {
			return resources.next();
		} else if (waitingResources.size() > 0) {
			return waitingResources.pollRandom();
		}

		throw new NoSuchElementException("there are no more resources");
	}
}