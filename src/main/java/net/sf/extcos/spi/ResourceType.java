package net.sf.extcos.spi;

public interface ResourceType {
	String getFileSuffix();
	ResourceAccessor getResourceAccessor();
}