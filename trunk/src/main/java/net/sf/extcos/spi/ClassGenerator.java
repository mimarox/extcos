package net.sf.extcos.spi;

import java.net.URL;

public interface ClassGenerator {
	Class<?> generateClass();
	
	/**
	 * This guaranteed to be called before {@link #generateClass()} is called.
	 * 
	 * @param resourceUrl The resource URL to set
	 */
	void setResourceUrl(URL resourceUrl);
}