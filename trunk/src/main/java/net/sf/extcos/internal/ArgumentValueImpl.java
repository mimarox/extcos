package net.sf.extcos.internal;

import net.sf.extcos.selector.annotation.ArgumentValue;
import net.sf.extcos.util.Assert;

public class ArgumentValueImpl implements ArgumentValue {
	private final Object value;

	public ArgumentValueImpl(final Object value) {
		Assert.notNull(value, IllegalArgumentException.class);
		this.value = value;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public boolean matches(@SuppressWarnings("hiding") final Object value) {
		return this.value.equals(value);
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
		ArgumentValueImpl other = (ArgumentValueImpl) obj;
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