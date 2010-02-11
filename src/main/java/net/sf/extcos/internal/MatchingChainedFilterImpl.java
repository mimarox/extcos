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

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class MatchingChainedFilterImpl extends AbstractChainedFilter implements
		MatchingChainedFilter {
	
	@Inject
	@Named("mcfi.waitingResources")
	private RandomPollingSet<Resource> waitingResources;
	
	private Iterator<Resource> resources;
	
	private ResourceMatcher resourceMatcher;

	public void setResourceMatcher(ResourceMatcher resourceMatcher) {
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
	protected Iterator<Resource> filter(Iterator<Resource> resources,
			Set<Resource> resultSet) {
		this.resources = resources;
		
		if (resourceMatcher == null) {
			logger.warn("resource matcher is not set, hence no filtering will take place");
			return resources;
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
		
		return resultSet.iterator();
	}

	//filter is leaf in filter tree and must therefore be an endpoint
	protected void filter(Iterator<Resource> resources,
			MultiplexingConnector resourceDispatcher) {
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
	protected Iterator<Resource> filter(Iterator<Resource> resources,
			MultiplexingConnector resourceDispatcher, Set<Resource> resultSet) {
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
			
			return resources;
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
		
		return resultSet.iterator();
	}
	
	private boolean hasNextResource() {
		return resources.hasNext() || waitingResources.size() > 0;
	}
	
	private Resource nextResource() {
		if (resources.hasNext() && waitingResources.size() > 0) {
			Random prng = new Random();
			if (prng.nextBoolean()) {
				return resources.next();
			} else {
				return waitingResources.pollRandom();
			}
		} else if (resources.hasNext()) {
			return resources.next();
		} else if (waitingResources.size() > 0) {
			return waitingResources.pollRandom();
		}
		
		throw new NoSuchElementException("there are no more resources");
	}
}