package net.sf.extcos.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import net.sf.extcos.collection.RandomPollingSet;
import net.sf.extcos.exception.UnsuccessfulOperationException;

/**
 * Resizable-array implementation of the <tt>RandomPollingSet</tt> interface.
 * Implements all optional Collection operations, and permits all elements,
 * excluding <tt>null</tt>.<p> 
 * 
 * In addition to implementing the <tt>RandomPollingSet</tt> interface,
 * this class provides methods to manipulate the size of the array of the
 * <tt>ArrayList</tt> that is used internally to store the set.<p>
 *
 * The <tt>size</tt>, <tt>isEmpty</tt>, <tt>get</tt>, <tt>set</tt>,
 * and <tt>iterator</tt> operations run in constant time. The <tt>add</tt>
 * operation runs in <i>amortized constant time</i>, that is, adding n elements
 * requires O(n) time.  All of the other operations run in linear time (roughly
 * speaking).<p>
 *
 * The <tt>ArrayList</tt> instance of each <tt>RandomPollingArraySet</tt>
 * instance has a <i>capacity</i>.  The capacity is the size of the array 
 * used to store the elements of the set.  It is always at least as large as
 * the set size. As elements are added to a <tt>RandomPollingArraySet</tt>,
 * its capacity grows automatically. The details of the growth policy are
 * not specified beyond the fact that adding an element has constant
 * amortized time cost.<p>
 *
 * An application can increase the capacity of a <tt>RandomPollingArraySet</tt>
 * instance before adding a large number of elements using the
 * <tt>ensureCapacity</tt> operation. This may reduce the amount of
 * incremental reallocation.
 *
 * <p><strong>Note that this implementation is not synchronized.</strong>
 * If multiple threads access a <tt>RandomPollingArraySet</tt> instance
 * concurrently, and at least one of the threads modifies the set
 * structurally, it <i>must</i> be synchronized externally.  (A structural
 * modification is any operation that adds or deletes one or more elements,
 * or explicitly resizes the array of the backing <tt>ArrayList</tt>; merely
 * setting the value of an element is not a structural modification.) This
 * is typically accomplished by synchronizing on some object that naturally
 * encapsulates the set.<p>
 *
 * If no such object exists, the set should be "wrapped" using the
 * {@link Collections#synchronizedSet Collections.synchronizedSet}
 * method.  This is best done at creation time, to prevent accidental
 * unsynchronized access to the set:<pre>
 *   Set set = Collections.synchronizedSet(new ArraySet(...));</pre>
 *
 * <p>The iterator returned by this class's <tt>iterator</tt> method is
 * not <i>fail-fast</i>: changes of any kind to the set will not result
 * in a {@link ConcurrentModificationException} being thrown. Therefore
 * the iterator does risk arbitrary, non-deterministic behavior at an
 * undetermined time in the future. This is acceptable because any iteration
 * over this set is non-deterministic anyhow.
 *
 * @author  Matthias Rothe
 * @see	    Collection
 * @see	    RandomPollingSet
 * @see	    ArraySet
 */
public class RandomPollingArraySet<E> extends ArraySet<E> implements
		RandomPollingSet<E> {
	
	private static final long serialVersionUID = -5304262039454673339L;
	
	private class Itr implements Iterator<E> {
		public boolean hasNext() {
			return size() > 0;
		}

		public E next() {
			if (hasNext()) {
				return pollRandom();
			} else {
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
	/**
	 * Constructs an empty set with an initial capacity of ten.
	 */
	public RandomPollingArraySet(){
	}
	
	/**
	 * Constructs an empty set with the specified initial capacity.
	 * 
	 * @param initialCapacity the initial capacity of the set
	 * @throws IllegalArgumentException if the specified initial capacity is negative
	 */
	public RandomPollingArraySet(int initialCapacity){
		super(initialCapacity);
	}
	
	/**
	 * Constructs a set containing the unique elements of the specified
	 * collection, in the order they are returned by the collection's iterator. 
	 *
	 * @param c the collection whose elements are to be placed into this list
	 * @throws NullPointerException if the specified collection is null
	 */
	public RandomPollingArraySet(Collection<? extends E> c){
		super(c);
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.util.AbstractCollection#iterator()
	 */
	public Iterator<E> iterator(){
		return new Itr();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jcs.collection.RandomPollingSet#pollRandom()
	 */
	public E pollRandom() {
		int size = size();
		
		if (size == 0) {
			throw new UnsuccessfulOperationException();
		} else if(size == 1) {
			return remove(0);
		} else {
			Random prng = new Random();
			double d = prng.nextDouble() * size;
			return remove(((int)d == size) ? size - 1 : (int)Math.floor(d));
		}
	}
}