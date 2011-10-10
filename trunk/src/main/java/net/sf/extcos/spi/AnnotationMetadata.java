package net.sf.extcos.spi;

public interface AnnotationMetadata {
	boolean hasKey(String key);

	/**
	 * Returns the value for the given key or <code>null</code> if key not present.
	 * 
	 * @param key The key for which the value is to be retrieved
	 * @return The value for the given key or <code>null</code> if key not present
	 */
	Object getValue(String key);
}