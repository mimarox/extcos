package net.sf.extcos.internal;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

public class ImplementingResourceMatcher implements ResourceMatcher {
	private Class<?> interfaze;
	
	public ImplementingResourceMatcher(Class<?> interfaze) {
		Assert.notNull(interfaze, IllegalArgumentException.class);
		Assert.isTrue(interfaze.isInterface(), IllegalArgumentException.class);
		this.interfaze = interfaze;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#matches(org.jcs.resource.Resource)
	 */
	public boolean matches(Resource resource) throws ConcurrentInspectionException {
		Assert.notNull(resource, IllegalArgumentException.class);
		return resource.hasInterface(interfaze);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#isMatcherFor(java.lang.Object)
	 */
	public boolean isMatcherFor(Object obj) {
		return interfaze.equals(obj);
	}
}