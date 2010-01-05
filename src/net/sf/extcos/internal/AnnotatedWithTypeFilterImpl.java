package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;

import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.annotation.ArgumentsDescriptor;
import net.sf.extcos.util.Assert;

public class AnnotatedWithTypeFilterImpl implements AnnotatedWithTypeFilter {
	private Class<? extends Annotation> annotation;
	private ArgumentsDescriptor arguments;

	public AnnotatedWithTypeFilterImpl(Class<? extends Annotation> annotation,
			ArgumentsDescriptor arguments) {
		Assert.notNull(annotation, iae());
		this.annotation = annotation;
		this.arguments = arguments;
	}

	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}

	public ArgumentsDescriptor getArguments() {
		return arguments;
	}
}