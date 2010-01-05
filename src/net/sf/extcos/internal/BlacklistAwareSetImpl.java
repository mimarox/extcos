package net.sf.extcos.internal;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Set;

import net.sf.extcos.collection.BlacklistAwareIterator;
import net.sf.extcos.collection.BlacklistAwareSet;
import net.sf.extcos.collection.IteratorCreationListener;
import net.sf.extcos.collection.RandomPollingSet;
import net.sf.extcos.exception.StateChangedException;
import net.sf.extcos.util.Assert;

public class BlacklistAwareSetImpl<E> implements BlacklistAwareSet<E> {
	
	private static class Itr<E> implements BlacklistAwareIterator<E> {
		private RandomPollingSet<E> backingSet;
		private Set<E> blacklist;
		private Object backingSetMutex = new Object();
		private Object blacklistMutex = new Object();
		private boolean stateChanged;

		private Itr(RandomPollingSet<E> backingSetSnapshot,
				Set<E> blacklistSnapshot) {
			this.backingSet = backingSetSnapshot;
			this.blacklist = blacklistSnapshot;
		}

		public boolean hasNext() {
			synchronized (blacklistMutex) {
				stateChanged = false;
				return backingSet.size() > blacklist.size();
			}
		}

		public E next() {
			if (stateChanged) {
				throw new StateChangedException();
			}
			
			E element = null;
			
			while (element == null && backingSet.size() > 0) {
				synchronized (backingSetMutex) {
					element = backingSet.pollRandom();
				}
				
				synchronized (blacklistMutex) {
					if (blacklist.contains(element)) {
						blacklist.remove(element);
						element = null;
					}
				}
			}
			
			if (element != null) {
				return element;
			} else {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

		public void addToBlacklist(E entry) {
			if (eligibleForBlacklisting(entry)) {
				synchronized (blacklistMutex) {
					blacklist.add(entry);
					stateChanged = true;
				}
			}
		}
		
		private boolean eligibleForBlacklisting(E entry) {
			synchronized (backingSetMutex) {
				return backingSet.contains(entry);
			}
		}
	}

	private RandomPollingSet<E> backingSet;

	private Set<E> blacklist;

	private IteratorCreationListener<E> iteratorCreationListener;
	
	public BlacklistAwareSetImpl(RandomPollingSet<E> backingSet,
			Set<E> blacklist) {
		Assert.notNull(backingSet, IllegalArgumentException.class);
		Assert.notNull(blacklist, IllegalArgumentException.class);
		
		backingSet.clear();
		blacklist.clear();
		
		this.backingSet = backingSet;
		this.blacklist = blacklist;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public BlacklistAwareIterator<E> iterator() {
		RandomPollingArraySet<E> backingSetCopy =
			new RandomPollingArraySet<E>(backingSet);
		
		ArraySet<E> blacklistCopy = new ArraySet<E>(blacklist);
		
		BlacklistAwareIterator<E> iterator =
			new Itr<E>(backingSetCopy, blacklistCopy);
		
		if (iteratorCreationListener != null) {
			iteratorCreationListener.created(iterator);
		}
		
		return iterator;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#size()
	 */
	public int size() {
		return backingSet.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#addToBlacklist(java.lang.Object)
	 */
	public boolean addToBlacklist(E entry) {
		if (contains(entry)) {
			return blacklist.add(entry);
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#setIteratorCreationListener(org.jcs.collection.IteratorCreationListener)
	 */
	public void setIteratorCreationListener(IteratorCreationListener<E> listener) {
		this.iteratorCreationListener = listener;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#add(java.lang.Object)
	 */
	public boolean add(E element) {
		return backingSet.add(element);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		return backingSet.addAll(c);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#clear()
	 */
	public void clear() {
		backingSet.clear();
		blacklist.clear();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#contains(java.lang.Object)
	 */
	public boolean contains(Object obj) {
		return backingSet.contains(obj);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		return backingSet.containsAll(c);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#remove(java.lang.Object)
	 */
	public boolean remove(Object obj) {
		if (contains(obj)) {
			backingSet.remove(obj);
			blacklist.remove(obj);
			return true;
		} else {
			return false;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toArray()
	 */
	public Object[] toArray() {
		return backingSet.toArray();
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toArray(T[])
	 */
	public <T> T[] toArray(T[] a) {
		return backingSet.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		return backingSet.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		
		for (Object obj : c) {
			changed |= remove(obj);
		}
		
		return changed;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		
		for (E element : backingSet) {
			if (!c.contains(element)) {
				changed |= remove(element);
			}
		}
		
		return changed;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#clearBlacklist()
	 */
	public void clearBlacklist() {
		blacklist.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#isBlacklisted(java.lang.Object)
	 */
	public boolean isBlacklisted(E element) {
		return backingSet.contains(element) && blacklist.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#removeFromBlacklist(java.lang.Object)
	 */
	public boolean removeFromBlacklist(E entry) {
		return blacklist.remove(entry);
	}
}