package net.sf.extcos.filter;

import java.lang.annotation.Annotation;

import net.sf.extcos.selector.annotation.ArgumentMapping;

public interface AnnotationArgument {
	Class<? extends Annotation> getAnnotation();
	ArgumentMapping getArgumentMapping();
	int hashCode();
	boolean equals(Object obj);
}