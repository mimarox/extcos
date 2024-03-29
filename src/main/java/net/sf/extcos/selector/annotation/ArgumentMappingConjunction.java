package net.sf.extcos.selector.annotation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.util.Assert;

public class ArgumentMappingConjunction implements ArgumentMappingJunction {
	private final Set<ArgumentMapping> mappings;

	public ArgumentMappingConjunction(final ArgumentMapping... mappings) {
		Assert.notEmpty(mappings, IllegalArgumentException.class);

		this.mappings = new HashSet<ArgumentMapping>(
				Arrays.asList(mappings));
	}

	@Override
	public boolean isSetIn(final AnnotationMetadata annotation) {
		for (ArgumentMapping mapping : mappings) {
			if (!mapping.isSetIn(annotation)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public Set<ArgumentMapping> getMappings() {
		return mappings;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (mappings == null ? 0 : mappings.hashCode());
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
		ArgumentMappingConjunction other = (ArgumentMappingConjunction) obj;
		if (mappings == null) {
			if (other.mappings != null) {
				return false;
			}
		} else if (!mappings.equals(other.mappings)) {
			return false;
		}
		return true;
	}
}