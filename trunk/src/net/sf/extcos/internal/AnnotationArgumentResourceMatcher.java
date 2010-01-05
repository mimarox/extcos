package net.sf.extcos.internal;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.AnnotationArgument;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.util.Assert;

public class AnnotationArgumentResourceMatcher implements ResourceMatcher {
	private AnnotationArgument annotationArgument;

	public AnnotationArgumentResourceMatcher(AnnotationArgument annotationArgument) {
		Assert.notNull(annotationArgument, IllegalArgumentException.class,
				"annotationArgument must not be null");
		
		this.annotationArgument = annotationArgument;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#matches(org.jcs.resource.Resource)
	 */
	public boolean matches(Resource resource) throws ConcurrentInspectionException {
		Assert.notNull(resource, IllegalArgumentException.class);
		
		AnnotationMetadata metadata = resource
				.getAnnotationMetadata(annotationArgument.getAnnotation());

		if (metadata != null) {
			return annotationArgument.getArgumentMapping().isSetIn(metadata);
		}
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#isMatcherFor(java.lang.Object)
	 */
	public boolean isMatcherFor(Object obj) {
		return annotationArgument.equals(obj);
	}
}