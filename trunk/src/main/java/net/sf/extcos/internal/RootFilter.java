package net.sf.extcos.internal;

import java.util.Iterator;
import java.util.Set;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.resource.Resource;

public class RootFilter extends AbstractChainedFilter {

	// not returning(all()), andStore() clause must have been set
	// or returning() clause with any of allExtending(), allImplementing()
	// allAnnotatedWith(), allBeing()
	protected Iterable<Resource> filter(Iterator<Resource> resources,
			Set<Resource> resultSet) {
		while (resources.hasNext()) {
			Resource resource = resources.next();
			
			try {
				if (resource.isClass()) {
					resultSet.add(resource);				
				}
			} catch (ConcurrentInspectionException ignored) {
				// should never be thrown, since we're not concurrent at this point
			}
		}
		
		return resultSet;
	}

	// returning(all()), no andStore() clause given
	protected void filter(Iterator<Resource> resources,
			MultiplexingConnector resourceDispatcher) {
		while (resources.hasNext()) {
			Resource resource = resources.next();
			
			try {
				if (resource.isClass()) {
					resourceDispatcher.connect(resource);				
				}
			} catch (ConcurrentInspectionException ignored) {
				// should never be thrown, since we're not concurrent at this point
			}
		}
	}

	// returning(all()), andStore() clause given
	protected Iterable<Resource> filter(Iterator<Resource> resources,
			MultiplexingConnector resourceDispatcher, Set<Resource> resultSet) {
		while (resources.hasNext()) {
			Resource resource = resources.next();
			
			try {
				if (resource.isClass()) {
					resultSet.add(resource);				
					resourceDispatcher.connect(resource);				
				}
			} catch (ConcurrentInspectionException ignored) {
				// should never be thrown, since we're not concurrent at this point
			}
		}
		
		return resultSet;
	}
}