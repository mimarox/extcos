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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((annotation == null) ? 0 : annotation.hashCode());
		result = prime * result
				+ ((arguments == null) ? 0 : arguments.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		AnnotatedWithTypeFilterImpl other = (AnnotatedWithTypeFilterImpl) obj;
		if (annotation == null) {
			if (other.annotation != null) {
				return false;
			}
		} else if (!annotation.equals(other.annotation)) {
			return false;
		}
		if (arguments == null) {
			if (other.arguments != null) {
				return false;
			}
		} else if (!arguments.equals(other.arguments)) {
			return false;
		}
		return true;
	}
}