package net.sf.extcos.filter;

public interface ChainedConnector extends MergableConnector {
	Connector getParentConnector();
	void setParentConnector(Connector connector);
	void setChildCount(int childCount);
}