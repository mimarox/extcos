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
	private List<Set<? super E>> slaves = new ArrayList<Set<? super E>>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcs.collection.MultiplexingSet#addSlaveSet(java.util.Set)
	 */
	public boolean addSlaveSet(Set<? super E> slave) {
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
	public void removeSets() {
		master = null;
		slaves.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcs.collection.MultiplexingSet#removeSlaveSet(java.util.Set)
	 */
	public boolean removeSlaveSet(Set<? super E> slave) {
		Assert.notNull(slave, iae());
		Assert.state(master != null);

		return slaves.remove(slave);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jcs.collection.MultiplexingSet#setMasterSet(java.util.Set)
	 */
	public void setMasterSet(Set<E> master) {
		Assert.notNull(master, iae());

		master.clear();
		this.master = master;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#add(java.lang.Object)
	 */
	public boolean add(E e) {
		Assert.state(master != null);

		if (master.add(e)) {
			for (Set<? super E> slave : slaves) {
				slave.add(e);
			}

			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	public boolean addAll(Collection<? extends E> c) {
		Assert.state(master != null);

		if (master.addAll(c)) {
			for (Set<? super E> slave : slaves) {
				slave.addAll(c);
			}

			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#clear()
	 */
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
	public boolean contains(Object obj) {
		Assert.state(master != null);
		return master.contains(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	public boolean containsAll(Collection<?> c) {
		Assert.state(master != null);
		return master.containsAll(c);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		Assert.state(master != null);
		return master.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#iterator()
	 */
	public Iterator<E> iterator() {
		Assert.state(master != null);
		return master.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	public boolean remove(Object obj) {
		Assert.state(master != null);

		if (master.remove(obj)) {
			for (Set<? super E> slave : slaves) {
				slave.remove(obj);
			}

			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	public boolean removeAll(Collection<?> c) {
		Assert.state(master != null);

		if (master.removeAll(c)) {
			for (Set<? super E> slave : slaves) {
				slave.removeAll(c);
			}

			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	public boolean retainAll(Collection<?> c) {
		Assert.state(master != null);

		List<E> remove = new ArrayList<E>();
		
		for (E e : master) {
			if (!c.contains(e)) remove.add(e);
		}
		
		if (remove.isEmpty()) {
			return false;
		} else {
			return removeAll(remove);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#size()
	 */
	public int size() {
		Assert.state(master != null);
		return master.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		Assert.state(master != null);
		return master.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#toArray(T[])
	 */
	public <T> T[] toArray(T[] array) {
		Assert.state(master != null);
		return master.toArray(array);
	}
}