package common;

import java.io.IOException;
import java.util.Properties;

public abstract class TestBase {
	private Properties properties;
	
	protected TestBase() {
		properties = new Properties();
		
		try {
			properties.load(getClass().getResourceAsStream("../test.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	protected int getIntProperty(String key) {
		return Integer.parseInt(properties.getProperty(key));
	}
}
