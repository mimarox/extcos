package net.sf.extcos.selector;

import static net.sf.extcos.util.Assert.*;

import net.sf.extcos.util.Assert;
import net.sf.extcos.util.StringUtils;

public class PackagePatternBuilder {
	private String subPackagePattern;
	
	public PackagePatternBuilder(String subPackagePattern) {
		Assert.isTrue(StringUtils.isJavaPackageWithWildcards(subPackagePattern), iae(),
				"subPackagePattern must be a valid Java Package name which may include wildcards, but [" +
				subPackagePattern + "] isn't!");
		this.subPackagePattern = subPackagePattern;
	}

	public String[] in(String... rootPackages) {
		Assert.notEmpty(rootPackages, iae(), "rootPackages must not be empty");
		
		String[] packagePatterns = new String[rootPackages.length];
		
		int i = 0;
		for (String rootPackage : rootPackages) {
			Assert.isTrue(StringUtils.isJavaPackage(rootPackage), iae(),
					"Any string contained in rootPackages must be a valid Java Package, but [" + rootPackage + "] isn't!");
			packagePatterns[i++] = rootPackage + "." + subPackagePattern;
		}
		
		return packagePatterns;
	}
}