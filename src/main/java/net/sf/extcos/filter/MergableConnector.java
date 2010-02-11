package net.sf.extcos.filter;

public interface MergableConnector extends Connector {
	void merge(Connector connector);
}