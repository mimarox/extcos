package net.sf.extcos.internal;

import net.sf.extcos.spi.ClassGenerator;
import net.sf.extcos.spi.ResourceAccessor;
import net.sf.extcos.spi.ResourceType;

public class JavaClassResourceType implements ResourceType {
	private static final String JAVA_CLASS_SUFFIX = "class";
	
	public String getFileSuffix() {
		return JAVA_CLASS_SUFFIX;
	}
	
	/**
	 * EDSL method
	 * 
	 * @return
	 */
	public static JavaClassResourceType javaClasses() {
	    return new JavaClassResourceType();
	}

	public ClassGenerator getClassGenerator() {
	    return new JavaClassGenerator();
	}

	public ResourceAccessor getResourceAccessor() {
		throw new UnsupportedOperationException("not yet implemented");
	}
}