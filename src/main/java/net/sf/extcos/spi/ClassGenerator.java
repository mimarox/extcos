package net.sf.extcos.spi;

import java.net.URL;

public interface ClassGenerator {
	Class<?> generateClass();
	void setResourceUrl(URL resourceUrl);
}