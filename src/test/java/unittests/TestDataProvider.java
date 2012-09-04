package unittests;

import org.testng.annotations.DataProvider;

import unittests.beans.PackagePatternDefinition;

public class TestDataProvider {
	
	@DataProvider
	public static Object[][] validPackages() {
		return new Object[][]{
			{"com.*", "net", "de.**"},
			{"org", "java.**.foo", "at.*.bar"},
			{"bar.first", "foo.second.third", "org.**.foo.*.bar"}
		};
	}
	
	@DataProvider
	public static Object[][] validPackagePatternDefinitions() {
		return new Object[][]{
			{new PackagePatternDefinition[]{
					new PackagePatternDefinition("**.foo.*.bar", new String[]{"com", "net.foo"})
			}},
			{new PackagePatternDefinition[]{
				new PackagePatternDefinition("*.foo", new String[]{"com", "net.foo"}),
				new PackagePatternDefinition("*.foo.**.bar.*", new String[]{"de.foo", "org"}),
			}}
		};
	}
}
