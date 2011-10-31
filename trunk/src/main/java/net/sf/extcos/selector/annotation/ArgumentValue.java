package net.sf.extcos.selector.annotation;

import net.sf.extcos.util.Assert;

public class ArgumentValue {
	private final Object value;

	public ArgumentValue(final Object value) {
		Assert.notNull(value, IllegalArgumentException.class);
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public boolean matches(final Object otherValue) {
		return this.value.equals(otherValue);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
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
		ArgumentValue other = (ArgumentValue) obj;
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