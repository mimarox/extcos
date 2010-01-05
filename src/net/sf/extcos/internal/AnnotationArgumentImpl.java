package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;

import net.sf.extcos.filter.AnnotationArgument;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.util.Assert;

public class AnnotationArgumentImpl implements AnnotationArgument {
	private Class<? extends Annotation> annotation;
	private ArgumentMapping mapping;
	
	/**
	 * @param annotation
	 * @param mapping
	 */
	public AnnotationArgumentImpl(Class<? extends Annotation> annotation,
			ArgumentMapping mapping) {
		Assert.notNull(annotation, iae());
		Assert.notNull(mapping, iae());
		
		this.annotation = annotation;
		this.mapping = mapping;
	}

	@Override
	public Class<? extends Annotation> getAnnotation() {
		return annotation;
	}

	@Override
	public ArgumentMapping getArgumentMapping() {
		return mapping;
	}

}
