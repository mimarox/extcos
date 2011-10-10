package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.util.Assert;

public class ImplementingTypeFilterImpl implements ImplementingTypeFilter {
	private final Set<Class<?>> interfaces;

	public ImplementingTypeFilterImpl(final Class<?>... interfaces) {
		Assert.notEmpty(interfaces, iae());

		this.interfaces = new HashSet<Class<?>>();

		for (Class<?> interfaze : interfaces) {
			Assert.isTrue(Modifier.isInterface(interfaze.getModifiers()), iae());
			this.interfaces.add(interfaze);
		}
	}

	@Override
	public Set<Class<?>> getInterfaces() {
		return interfaces;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (interfaces == null ? 0 : interfaces.hashCode());
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
		ImplementingTypeFilterImpl other = (ImplementingTypeFilterImpl) obj;
		if (interfaces == null) {
			if (other.interfaces != null) {
				return false;
			}
		} else if (!interfaces.equals(other.interfaces)) {
			return false;
		}
		return true;
	}
}