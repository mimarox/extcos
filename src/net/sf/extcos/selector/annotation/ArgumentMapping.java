package net.sf.extcos.selector.annotation;

import net.sf.extcos.spi.AnnotationMetadata;

/**
 * Maps an argument key of an annotation to an expected value.
 * 
 * @author Matthias Rothe
 */
public interface ArgumentMapping {
	boolean isSetIn(AnnotationMetadata annotation);
}