package net.sf.extcos.collection;

import java.util.Set;

public interface MultiplexingSet<E> extends Set<E> {

	/**
	 * Sets the master set. The performance of the master set determines the
	 * performance of this set for all read operations.
	 * 
	 * @param master
	 *            The master set to set
	 */
	void setMasterSet(Set<E> master);

	/**
	 * Removes the master and all slave sets.
	 * <p>
	 * Subsequent reads and writes will throw an {@link IllegalStateException}.
	 * Setting a new master set puts this set back into normal working mode.
	 */
	void removeSets();

	/**
	 * Adds a slave set to this multiplexing set. Depending on the
	 * implementation this might succeed if the slave is reinserted, or it might
	 * not.
	 * 
	 * @param slaveSet
	 *            The slave set to add
	 * @return true, if and only if the given slave set was added successfully,
	 *         false otherwise
	 */
	boolean addSlaveSet(Set<? super E> slave);

	/**
	 * Removes the given slave set from this multiplexing set.
	 * 
	 * @param slave
	 *            The slave set to remove
	 * @return true, if and only if the given slave set was removed
	 *         successfully, false otherwise
	 */
	boolean removeSlaveSet(Set<? super E> slave);
}