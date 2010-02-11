package net.sf.extcos.collection;


public interface IteratorCreationListener<E> {
	void created(BlacklistAwareIterator<E> iterator);
}