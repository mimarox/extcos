package net.sf.extcos.selector.annotation;

public interface ArgumentValue {
	Object getValue();
	boolean matches(Object value);
	int hashCode();
	boolean equals(Object obj);
}