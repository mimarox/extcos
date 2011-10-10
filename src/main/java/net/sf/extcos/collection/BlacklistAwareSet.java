package net.sf.extcos.collection;

import java.util.Iterator;
import java.util.Set;

public interface BlacklistAwareSet<E> extends Set<E> {
	/**
	 * Adds an entry to this set's blacklist.
	 * 
	 * @param entry
	 *            The entry to add to the blacklist
	 */
	boolean addToBlacklist(E entry);

	/**
	 * Checks whether the given element is blacklisted
	 * 
	 * @param element
	 *            The element to be checked
	 * @return true, if and only if the given element is contained in this set
	 *         and is an entry in the blacklist, false otherwise
	 */
	boolean isBlacklisted(E element);

	/**
	 * Removes the given entry from the blacklist, if it is contained by it.
	 * 
	 * @param entry
	 * The entry to be removed
	 * @return true, if the entry was contained in the blacklist and was
	 * successfully removed, false otherwise
	 */
	boolean removeFromBlacklist(E entry);

	/**
	 * Removes all entries from the blacklist. The blacklist will be empty
	 * when this method returns.
	 */
	void clearBlacklist();

	/**
	 * Returns an iterator over the elements that were in this set at the
	 * creation of the iterator in the sequence defined by the underlying
	 * {@link Set} implementation. Any element returned by the iterator's
	 * {@link Iterator#next() next()} method is guaranteed to not be an entry of
	 * this set's blacklist.
	 * <p>
	 * Any structural changes to this set that take place after calling this
	 * method will not alter the behaviour of the returned iterator. This is
	 * because upon creation the iterator takes a snapshot of this set and only
	 * iterates over that snapshot, being completely detached from this set.
	 * <p>
	 * The {@link Iterator#remove()} method is not supported by the returned
	 * iterator and will throw an {@link UnsupportedOperationException} when
	 * called.
	 */
	@Override
	BlacklistAwareIterator<E> iterator();

	/**
	 * Sets a callback to be notified upon the creation of a new iterator by
	 * calling the {@link #iterator()} method.
	 * 
	 * @param listener
	 *            the callback
	 */
	void setIteratorCreationListener(IteratorCreationListener<E> listener);
}