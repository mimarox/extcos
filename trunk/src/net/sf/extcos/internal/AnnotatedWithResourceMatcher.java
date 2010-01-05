package net.sf.extcos.internal;

import java.lang.annotation.Annotation;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

public class AnnotatedWithResourceMatcher implements ResourceMatcher {
	private Class<? extends Annotation> annotation;

	public AnnotatedWithResourceMatcher(Class<? extends Annotation> annotation) {
		Assert.notNull(annotation, IllegalArgumentException.class,
			"annotation must not be null");

		this.annotation = annotation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#matches(org.jcs.resource.Resource)
	 */
	public boolean matches(Resource resource) throws ConcurrentInspectionException {
		Assert.notNull(resource, IllegalArgumentException.class);
		return resource.getAnnotationMetadata(annotation) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#isMatcherFor(java.lang.Object)
	 */
	public boolean isMatcherFor(Object obj) {
		return annotation.equals(obj);
	}
}