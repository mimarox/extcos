package net.sf.extcos.collection;

import java.util.Iterator;

public interface BlacklistAwareIterator<E> extends Iterator<E> {
	void addToBlacklist(E entry);
}