package net.sf.extcos.util;

import java.lang.reflect.Array;

/**
 * This class provides utility methods to work with arrays.
 * 
 * @author Matthias Rothe
 */
public class ArrayUtils {
	private ArrayUtils() {
	} // so it can't be instantiated

	public static <T> T[] join(T[]... arrays) {
		int totalSize = 0;
		
		for (T[] array : arrays) {
			totalSize += array.length;
		}
		
		@SuppressWarnings("unchecked")
		T[] joined = (T[]) Array.newInstance(
				getRootComponentType(arrays), totalSize);
		
		int i = 0;
		for (T[] array : arrays) {
			System.arraycopy(array, 0, joined, i, array.length);
			i += array.length;
		}
		
		return joined;
	}
	
	public static Class<?> getRootComponentType(Object array) {
		Class<?> clazz = array.getClass();
		
		while (clazz.isArray()) {
			clazz = clazz.getComponentType();
		}
		
		return clazz;
	}
}
