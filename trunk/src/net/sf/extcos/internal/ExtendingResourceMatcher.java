package net.sf.extcos.internal;

import java.lang.reflect.Modifier;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

public class ExtendingResourceMatcher implements ResourceMatcher {
	private Class<?> clazz;

	public ExtendingResourceMatcher(Class<?> clazz) {
		Assert.notNull(clazz, IllegalArgumentException.class);
		Assert.isFalse(clazz.isInterface(), IllegalArgumentException.class);
		Assert.isFalse(Modifier.isFinal(clazz.getModifiers()),
				IllegalArgumentException.class);
		
		this.clazz = clazz;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#matches(org.jcs.resource.Resource)
	 */
	public boolean matches(Resource resource) throws ConcurrentInspectionException {
		Assert.notNull(resource, IllegalArgumentException.class);
		return resource.isSubclassOf(clazz);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResourceMatcher#isMatcherFor(java.lang.Object)
	 */
	public boolean isMatcherFor(Object obj) {
		return clazz.equals(obj);
	}
}