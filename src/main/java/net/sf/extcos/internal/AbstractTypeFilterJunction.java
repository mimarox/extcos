package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypeFilterJunction;

public class AbstractTypeFilterJunction implements TypeFilterJunction {
	protected Set<TypeFilter> typeFilters;

	@Override
	public Set<TypeFilter> getTypeFilters() {
		return typeFilters;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (typeFilters == null ? 0 : typeFilters.hashCode());
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
		TypeFilterConjunction other = (TypeFilterConjunction) obj;
		if (typeFilters == null) {
			if (other.typeFilters != null) {
				return false;
			}
		} else if (!typeFilters.equals(other.typeFilters)) {
			return false;
		}
		return true;
	}
}
