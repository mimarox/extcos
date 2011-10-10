package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;

import net.sf.extcos.filter.AnnotationArgument;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.util.Assert;

public class AnnotationArgumentImpl implements AnnotationArgument {
	private final Class<? extends Annotation> annotation;
	private final ArgumentMapping mapping;

	public AnnotationArgumentImpl(final Class<? extends Annotation> annotation,
			final ArgumentMapping mapping) {
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (annotation == null ? 0 : annotation.hashCode());
		result = prime * result + (mapping == null ? 0 : mapping.hashCode());
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
		AnnotationArgumentImpl other = (AnnotationArgumentImpl) obj;
		if (annotation == null) {
			if (other.annotation != null) {
				return false;
			}
		} else if (!annotation.equals(other.annotation)) {
			return false;
		}
		if (mapping == null) {
			if (other.mapping != null) {
				return false;
			}
		} else if (!mapping.equals(other.mapping)) {
			return false;
		}
		return true;
	}

}