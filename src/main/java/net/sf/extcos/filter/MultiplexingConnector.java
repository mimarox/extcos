package net.sf.extcos.filter;

public interface MultiplexingConnector extends MergableConnector {
	void addConnector(Connector connector);
}