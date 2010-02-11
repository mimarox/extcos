package net.sf.extcos.spi;


public interface ResourceType {
	String getFileSuffix();
	ClassGenerator getClassGenerator();
	ResourceAccessor getResourceAccessor();
}