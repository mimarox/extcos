package net.sf.extcos.internal;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import net.sf.extcos.collection.BlacklistAwareIterator;
import net.sf.extcos.collection.BlacklistAwareSet;
import net.sf.extcos.collection.IteratorCreationListener;
import net.sf.extcos.collection.RandomPollingSet;
import net.sf.extcos.filter.BlacklistManager;
import net.sf.extcos.resource.Resource;

import com.google.inject.Singleton;

@Singleton
public class BlacklistManagerImpl implements BlacklistManager {
	private ConcurrentSkipListSet<SoftReference<BlacklistAwareSet<Resource>>> managedSets =
		new ConcurrentSkipListSet<SoftReference<BlacklistAwareSet<Resource>>>();
	private ReferenceQueue<BlacklistAwareSet<Resource>> setReferenceQueue =
		new ReferenceQueue<BlacklistAwareSet<Resource>>();
	
	private ConcurrentSkipListSet<SoftReference<BlacklistAwareIterator<Resource>>> managedIterators =
		new ConcurrentSkipListSet<SoftReference<BlacklistAwareIterator<Resource>>>();
	private ReferenceQueue<BlacklistAwareIterator<Resource>> iteratorReferenceQueue =
		new ReferenceQueue<BlacklistAwareIterator<Resource>>();
	
	public synchronized void blacklist(Resource resource) {
		blacklistOnIterators(resource);
		blacklistOnSets(resource);
	}

	private void blacklistOnIterators(Resource resource) {
		Iterator<SoftReference<BlacklistAwareIterator<Resource>>> iteratorIterator =
			managedIterators.iterator();
		
		while (iteratorIterator.hasNext()) {
			SoftReference<BlacklistAwareIterator<Resource>> reference =
				iteratorIterator.next();
			
			BlacklistAwareIterator<Resource> iterator = reference.get();
			
			if (iterator == null || reference.isEnqueued()) {
				iteratorIterator.remove();
			} else {
				iterator.addToBlacklist(resource);
			}
		}
	}
	
	private void blacklistOnSets(Resource resource) {
		Iterator<SoftReference<BlacklistAwareSet<Resource>>> setIterator =
			managedSets.iterator();
		
		while (setIterator.hasNext()) {
			SoftReference<BlacklistAwareSet<Resource>> reference =
				setIterator.next();
			
			BlacklistAwareSet<Resource> iterator = reference.get();
			
			if (iterator == null || reference.isEnqueued()) {
				setIterator.remove();
			} else {
				iterator.addToBlacklist(resource);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.BlacklistManager#newResultSet()
	 */
	public Set<Resource> newResultSet() {
		RandomPollingSet<Resource> resources =
			new RandomPollingArraySet<Resource>();
		
		ArraySet<Resource> blacklist = new ArraySet<Resource>();
		
		BlacklistAwareSet<Resource> set =
			new BlacklistAwareSetImpl<Resource>(resources, blacklist);
		
		set.setIteratorCreationListener(
				new IteratorCreationListener<Resource>() {
					public void created(BlacklistAwareIterator<Resource> iterator) {
						manageIterator(iterator);
					}
		});
		
		managedSets.add(new SoftReference<BlacklistAwareSet<Resource>>(set,
				setReferenceQueue));
		
		return set;
	}
	
	private void manageIterator(BlacklistAwareIterator<Resource> iterator) {
		managedIterators.add(new SoftReference<BlacklistAwareIterator<Resource>>(iterator, iteratorReferenceQueue));
	}
}