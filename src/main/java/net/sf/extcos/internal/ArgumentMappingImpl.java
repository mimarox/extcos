package net.sf.extcos.internal;

import net.sf.extcos.selector.annotation.ArgumentKey;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentValue;
import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.util.Assert;

public class ArgumentMappingImpl implements ArgumentMapping {
	private final String key;
	private final ArgumentValue value;

	public ArgumentMappingImpl(final ArgumentKey key, final ArgumentValue value) {
		Assert.notNull(key, IllegalArgumentException.class);
		Assert.notNull(value, IllegalArgumentException.class);

		this.key = key.getKey();
		this.value = value;
	}

	@Override
	public boolean isSetIn(final AnnotationMetadata annotation) {
		if (annotation.hasKey(key)) {
			@SuppressWarnings("hiding")
			Object value = annotation.getValue(key);

			if (value != null) {
				return this.value.matches(value);
			}
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (key == null ? 0 : key.hashCode());
		result = prime * result + (value == null ? 0 : value.hashCode());
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
		ArgumentMappingImpl other = (ArgumentMappingImpl) obj;
		if (key == null) {
			if (other.key != null) {
				return false;
			}
		} else if (!key.equals(other.key)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}
}