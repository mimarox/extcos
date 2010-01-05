package net.sf.extcos.spi;

public interface AnnotationMetadata {
	boolean hasKey(String key);
	
	/**
	 * Return null if key not present
	 * @param key
	 * @return
	 */
	Object getValue(String key);
}