package net.sf.extcos.spi;

import java.lang.annotation.Annotation;
import java.net.URL;

public interface ResourceAccessor {
	
	/**
	 * This guaranteed to be called before any other method is called.
	 * 
	 * @param resourceUrl The resource URL to set
	 */
	void setResourceUrl(URL resourceUrl);

	AnnotationMetadata getAnnotationMetadata(
			Class<? extends Annotation> annotation);

	boolean hasInterface(Class<?> interfaze);

	boolean isSubclassOf(Class<?> clazz);

	boolean isClass();
	
	Class<?> generateClass();
}