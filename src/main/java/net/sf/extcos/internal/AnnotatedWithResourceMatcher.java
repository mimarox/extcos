package net.sf.extcos.internal;

import java.lang.annotation.Annotation;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

public class AnnotatedWithResourceMatcher implements ResourceMatcher {
	private final Class<? extends Annotation> annotation;

	public AnnotatedWithResourceMatcher(final Class<? extends Annotation> annotation) {
		Assert.notNull(annotation, IllegalArgumentException.class,
				"annotation must not be null");

		this.annotation = annotation;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#matches(org.jcs.resource.Resource)
	 */
	@Override
	public boolean matches(final Resource resource) throws ConcurrentInspectionException {
		Assert.notNull(resource, IllegalArgumentException.class);
		return resource.getAnnotationMetadata(annotation) != null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#isMatcherFor(java.lang.Object)
	 */
	@Override
	public boolean isMatcherFor(final Object obj) {
		return annotation.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (annotation == null ? 0 : annotation.hashCode());
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
		AnnotatedWithResourceMatcher other = (AnnotatedWithResourceMatcher) obj;
		if (annotation == null) {
			if (other.annotation != null) {
				return false;
			}
		} else if (!annotation.equals(other.annotation)) {
			return false;
		}
		return true;
	}
}