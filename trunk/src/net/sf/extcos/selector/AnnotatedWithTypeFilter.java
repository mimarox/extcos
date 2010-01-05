package net.sf.extcos.selector;

import java.lang.annotation.Annotation;

import net.sf.extcos.selector.annotation.ArgumentsDescriptor;

public interface AnnotatedWithTypeFilter extends MultipleTypeFilter {
	Class<? extends Annotation> getAnnotation();
	ArgumentsDescriptor getArguments();
}