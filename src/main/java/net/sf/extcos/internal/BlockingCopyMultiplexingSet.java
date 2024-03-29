package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sf.extcos.collection.MultiplexingSet;
import net.sf.extcos.util.Assert;

public class BlockingCopyMultiplexingSet<E> implements MultiplexingSet<E> {
	private Set<E> master;
	private final List<Set<? super E>> slaves = new ArrayList<Set<? super E>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcs.collection.MultiplexingSet#addSlaveSet(java.util.Set)
	 */
	@Override
	public boolean addSlaveSet(final Set<? super E> slave) {
		Assert.notNull(slave, iae());
		Assert.state(master != null);

		slave.clear();

		return slaves.add(slave);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcs.collection.MultiplexingSet#removeSets()
	 */
	@Override
	public void removeSets() {
		master = null;
		slaves.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcs.collection.MultiplexingSet#removeSlaveSet(java.util.Set)
	 */
	@Override
	public boolean removeSlaveSet(final Set<? super E> slave) {
		Assert.notNull(slave, iae());
		Assert.state(master != null);

		return slaves.remove(slave);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcs.collection.MultiplexingSet#setMasterSet(java.util.Set)
	 */
	@Override
	public void setMasterSet(final Set<E> master) {
		Assert.notNull(master, iae());

		master.clear();
		this.master = master;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(final E e) {
		Assert.state(master != null);

		if (master.add(e)) {
			for (Set<? super E> slave : slaves) {
				slave.add(e);
			}

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(final Collection<? extends E> c) {
		Assert.state(master != null);

		if (master.addAll(c)) {
			for (Set<? super E> slave : slaves) {
				slave.addAll(c);
			}

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		Assert.state(master != null);

		master.clear();

		for (Set<? super E> slave : slaves) {
			slave.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(final Object obj) {
		Assert.state(master != null);
		return master.contains(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		Assert.state(master != null);
		return master.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		Assert.state(master != null);
		return master.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
		Assert.state(master != null);
		return master.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(final Object obj) {
		Assert.state(master != null);

		if (master.remove(obj)) {
			for (Set<? super E> slave : slaves) {
				slave.remove(obj);
			}

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		Assert.state(master != null);

		if (master.removeAll(c)) {
			for (Set<? super E> slave : slaves) {
				slave.removeAll(c);
			}

			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		Assert.state(master != null);

		List<E> remove = new ArrayList<E>();

		for (E e : master) {
			if (!c.contains(e)) {
				remove.add(e);
			}
		}

		if (remove.isEmpty()) {
			return false;
		}

		return removeAll(remove);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
		Assert.state(master != null);
		return master.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		Assert.state(master != null);
		return master.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray(T[])
	 */
	@Override
	public <T> T[] toArray(final T[] array) {
		Assert.state(master != null);
		return master.toArray(array);
	}
}