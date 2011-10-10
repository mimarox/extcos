package net.sf.extcos.internal;

import net.sf.extcos.spi.ResourceAccessor;
import net.sf.extcos.spi.ResourceType;

public class JavaClassResourceType implements ResourceType {
	private static final String JAVA_CLASS_SUFFIX = "class";
	private static JavaClassResourceType instance;

	/**
	 * Always instantiate via the {@link #javaClasses()} method.
	 */
	private JavaClassResourceType() {
	}

	@Override
	public String getFileSuffix() {
		return JAVA_CLASS_SUFFIX;
	}

	/**
	 * EDSL method
	 */
	public static JavaClassResourceType javaClasses() {
		if (instance == null) {
			instance = new JavaClassResourceType();
		}

		return instance;
	}

	@Override
	public ResourceAccessor getResourceAccessor() {
		return new JavaResourceAccessor();
	}
}