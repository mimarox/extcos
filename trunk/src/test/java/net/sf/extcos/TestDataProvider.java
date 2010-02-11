package net.sf.extcos;

import org.testng.annotations.DataProvider;

public class TestDataProvider {
	
	@DataProvider
	public static Object[][] validPackages() {
		return new Object[][]{{"com", "net", "de"}, {"org", "java", "at"}};
	}
}
