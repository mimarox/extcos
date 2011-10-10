package net.sf.extcos.selector.annotation;

public interface ArgumentValue {
	Object getValue();
	boolean matches(Object value);

	@Override
	int hashCode();

	@Override
	boolean equals(Object obj);
}