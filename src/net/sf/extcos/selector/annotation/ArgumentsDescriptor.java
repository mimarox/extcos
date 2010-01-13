package net.sf.extcos.selector.annotation;


public interface ArgumentsDescriptor {
	ArgumentMapping getArgumentMapping();
	int hashCode();
	boolean equals(Object obj);
}