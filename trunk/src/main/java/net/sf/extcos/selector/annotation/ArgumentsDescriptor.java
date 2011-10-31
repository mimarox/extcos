package net.sf.extcos.selector.annotation;

import static net.sf.extcos.util.Assert.iae;
import net.sf.extcos.internal.ArgumentMappingImpl;
import net.sf.extcos.util.Assert;

public class ArgumentsDescriptor {
	private final ArgumentMapping mapping;

	public ArgumentsDescriptor(final ArgumentKey key, final ArgumentValue value) {
		Assert.notNull(key, iae());
		Assert.notNull(value, iae());
		mapping = new ArgumentMappingImpl(key, value);
	}

	public ArgumentsDescriptor(final ArgumentMapping mapping) {
		Assert.notNull(mapping, iae());
		this.mapping = mapping;
	}

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
		ArgumentsDescriptor other = (ArgumentsDescriptor) obj;
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