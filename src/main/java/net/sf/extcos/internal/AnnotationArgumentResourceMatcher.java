package net.sf.extcos.internal;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.AnnotationArgument;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.util.Assert;

public class AnnotationArgumentResourceMatcher implements ResourceMatcher {
	private final AnnotationArgument annotationArgument;

	public AnnotationArgumentResourceMatcher(final AnnotationArgument annotationArgument) {
		Assert.notNull(annotationArgument, IllegalArgumentException.class,
				"annotationArgument must not be null");

		this.annotationArgument = annotationArgument;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#matches(org.jcs.resource.Resource)
	 */
	@Override
	public boolean matches(final Resource resource) throws ConcurrentInspectionException {
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
	@Override
	public boolean isMatcherFor(final Object obj) {
		return annotationArgument.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ (annotationArgument == null ? 0 : annotationArgument
						.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AnnotationArgumentResourceMatcher other = (AnnotationArgumentResourceMatcher) obj;
		if (annotationArgument == null) {
			if (other.annotationArgument != null) {
				return false;
			}
		} else if (!annotationArgument.equals(other.annotationArgument)) {
			return false;
		}
		return true;
	}
}