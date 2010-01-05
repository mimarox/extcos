package net.sf.extcos.internal;

import java.util.Iterator;
import java.util.Set;

import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.resource.Resource;

public class RootFilter extends AbstractChainedFilter {

	// not returning(all()), andStore() clause must have been set
	// or returning() clause with any of allExtending(), allImplementing()
	// allAnnotatedWith(), allBeing()
	protected Iterator<Resource> filter(Iterator<Resource> resources,
			Set<Resource> resultSet) {
		while (resources.hasNext()) {
			Resource resource = resources.next();
			
			if (resource.isClass()) {
				resultSet.add(resource);				
			}
		}
		
		return resultSet.iterator();
	}

	// returning(all()), no andStore() clause given
	protected void filter(Iterator<Resource> resources,
			MultiplexingConnector resourceDispatcher) {
		while (resources.hasNext()) {
			Resource resource = resources.next();
			
			if (resource.isClass()) {
				resourceDispatcher.connect(resource);				
			}
		}
	}

	// returning(all()), andStore() clause given
	protected Iterator<Resource> filter(Iterator<Resource> resources,
			MultiplexingConnector resourceDispatcher, Set<Resource> resultSet) {
		while (resources.hasNext()) {
			Resource resource = resources.next();
			
			if (resource.isClass()) {
				resultSet.add(resource);				
				resourceDispatcher.connect(resource);				
			}
		}
		
		return resultSet.iterator();
	}
}