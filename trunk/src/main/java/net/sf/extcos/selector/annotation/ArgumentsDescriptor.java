package net.sf.extcos.selector.annotation;

public interface ArgumentsDescriptor {
	ArgumentMapping getArgumentMapping();

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);
}