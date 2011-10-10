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
		private final RandomPollingSet<E> backingSet;
		private final Set<E> blacklist;
		private final Object backingSetMutex = new Object();
		private final Object blacklistMutex = new Object();
		private boolean stateChanged;

		private Itr(final RandomPollingSet<E> backingSetSnapshot,
				final Set<E> blacklistSnapshot) {
			this.backingSet = backingSetSnapshot;
			this.blacklist = blacklistSnapshot;
		}

		@Override
		public boolean hasNext() {
			synchronized (blacklistMutex) {
				stateChanged = false;
				return backingSet.size() > blacklist.size();
			}
		}

		@Override
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
			}

			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addToBlacklist(final E entry) {
			if (eligibleForBlacklisting(entry)) {
				synchronized (blacklistMutex) {
					blacklist.add(entry);
					stateChanged = true;
				}
			}
		}

		private boolean eligibleForBlacklisting(final E entry) {
			synchronized (backingSetMutex) {
				return backingSet.contains(entry);
			}
		}
	}

	private final RandomPollingSet<E> backingSet;

	private final Set<E> blacklist;

	private IteratorCreationListener<E> iteratorCreationListener;

	public BlacklistAwareSetImpl(final RandomPollingSet<E> backingSet,
			final Set<E> blacklist) {
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
	@Override
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
	@Override
	public int size() {
		return backingSet.size();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#addToBlacklist(java.lang.Object)
	 */
	@Override
	public boolean addToBlacklist(final E entry) {
		if (contains(entry)) {
			return blacklist.add(entry);
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#setIteratorCreationListener(org.jcs.collection.IteratorCreationListener)
	 */
	@Override
	public void setIteratorCreationListener(final IteratorCreationListener<E> listener) {
		this.iteratorCreationListener = listener;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#add(java.lang.Object)
	 */
	@Override
	public boolean add(final E element) {
		return backingSet.add(element);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends E> c) {
		return backingSet.addAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#clear()
	 */
	@Override
	public void clear() {
		backingSet.clear();
		blacklist.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object obj) {
		return backingSet.contains(obj);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		return backingSet.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object obj) {
		if (contains(obj)) {
			backingSet.remove(obj);
			blacklist.remove(obj);
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toArray()
	 */
	@Override
	public Object[] toArray() {
		return backingSet.toArray();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(final T[] a) {
		return backingSet.toArray(a);
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return backingSet.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
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
	@Override
	public boolean retainAll(final Collection<?> c) {
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
	@Override
	public void clearBlacklist() {
		blacklist.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#isBlacklisted(java.lang.Object)
	 */
	@Override
	public boolean isBlacklisted(final E element) {
		return backingSet.contains(element) && blacklist.contains(element);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.BlacklistAwareSet#removeFromBlacklist(java.lang.Object)
	 */
	@Override
	public boolean removeFromBlacklist(final E entry) {
		return blacklist.remove(entry);
	}
}