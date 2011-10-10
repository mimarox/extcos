package net.sf.extcos.internal;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

public class ImplementingResourceMatcher implements ResourceMatcher {
	private final Class<?> interfaze;

	public ImplementingResourceMatcher(final Class<?> interfaze) {
		Assert.notNull(interfaze, IllegalArgumentException.class);
		Assert.isTrue(interfaze.isInterface(), IllegalArgumentException.class);
		this.interfaze = interfaze;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#matches(org.jcs.resource.Resource)
	 */
	@Override
	public boolean matches(final Resource resource) throws ConcurrentInspectionException {
		Assert.notNull(resource, IllegalArgumentException.class);
		return resource.hasInterface(interfaze);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#isMatcherFor(java.lang.Object)
	 */
	@Override
	public boolean isMatcherFor(final Object obj) {
		return interfaze.equals(obj);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ (interfaze == null ? 0 : interfaze.hashCode());
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
		ImplementingResourceMatcher other = (ImplementingResourceMatcher) obj;
		if (interfaze == null) {
			if (other.interfaze != null) {
				return false;
			}
		} else if (!interfaze.equals(other.interfaze)) {
			return false;
		}
		return true;
	}
}