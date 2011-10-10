package net.sf.extcos.collection;

import java.util.Iterator;
import java.util.Queue;
import java.util.Set;

import net.sf.extcos.internal.RandomPollingArraySet;

public interface RandomPollingSet<E> extends Set<E> {

	/**
	 * Returns an iterator over the elements in this set in random sequence.
	 * Elements returned by the iterator's {@link Iterator#next() next()}
	 * method will be polled from this set. If the iterator's
	 * {@link Iterator#hasNext() hasNext()} method returns <tt>false</tt>
	 * this set's {@link RandomPollingArraySet#size() size()} method will
	 * return <tt>0</tt>, meaning the set will be empty after one complete
	 * iteration.
	 * <p>
	 * The {@link Iterator#remove()} method is not supported by the returned
	 * iterator and will throw an {@link UnsupportedOperationException} when
	 * called.
	 * 
	 * @return an iterator over the elements in this set in random sequence
	 */
	@Override
	Iterator<E> iterator();

	/**
	 * Polls a random element from this set.
	 * <p>
	 * As is the case with {@link Queue#poll() Queues}, polling means
	 * removing the polled element from the collection.
	 * 
	 * @return the randomly polled element
	 */
	E pollRandom();
}