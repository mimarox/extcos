package net.sf.extcos.spi;

import java.lang.annotation.Annotation;
import java.net.URL;

public interface ResourceAccessor {
	void setResourceUrl(URL resourceUrl);

	AnnotationMetadata getAnnotationMetadata(
			Class<? extends Annotation> annotation);

	boolean hasInterface(Class<?> interfaze);

	boolean isSubclassOf(Class<?> clazz);

	boolean isClass();
}