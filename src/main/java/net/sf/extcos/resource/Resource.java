package net.sf.extcos.resource;

import java.lang.annotation.Annotation;

import net.sf.extcos.classgeneration.ClassGenerationListener;
import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.spi.AnnotationMetadata;

public interface Resource {

	/**
	 * Retrieves the AnnotationMetadata for the given annotation if it is
	 * present at this resource. Returns null if the given annotation is not
	 * present at this resource.
	 * 
	 * @param annotation
	 *            The annotation for which to retrieve the metadata
	 * 
	 * @return the AnnotationMetadata or null
	 */
	AnnotationMetadata getAnnotationMetadata(
			Class<? extends Annotation> annotation)
			throws ConcurrentInspectionException;

	boolean isClass() throws ConcurrentInspectionException;

	boolean isSubclassOf(Class<?> clazz) throws ConcurrentInspectionException;

	boolean hasInterface(Class<?> interfaze) throws ConcurrentInspectionException;

	void generateAndDispatchClass();

	void addClassGenerationListener(ClassGenerationListener listener);
}