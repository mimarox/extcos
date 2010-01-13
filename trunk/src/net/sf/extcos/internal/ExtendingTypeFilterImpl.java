package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.reflect.Modifier;

import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.util.Assert;

public class ExtendingTypeFilterImpl implements ExtendingTypeFilter {
	private Class<?> clazz;
	
	public ExtendingTypeFilterImpl(Class<?> clazz) {
		Assert.notNull(clazz, iae());
		
		int modifiers = clazz.getModifiers();
		Assert.isFalse(Modifier.isInterface(modifiers), iae());
		Assert.isFalse(Modifier.isFinal(modifiers), iae());
		
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clazz == null) ? 0 : clazz.hashCode());
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
		ExtendingTypeFilterImpl other = (ExtendingTypeFilterImpl) obj;
		if (clazz == null) {
			if (other.clazz != null) {
				return false;
			}
		} else if (!clazz.equals(other.clazz)) {
			return false;
		}
		return true;
	}
}