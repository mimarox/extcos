package unittests.beans;

import java.util.Arrays;

public class PackagePatternDefinition {
	private String subPackagePattern;
	private String[] rootPackages;

	public PackagePatternDefinition() {
	}

	public PackagePatternDefinition(String subPackagePattern,
			String[] rootPackages) {
		this.subPackagePattern = subPackagePattern;
		this.rootPackages = rootPackages;
	}

	public String getSubPackagePattern() {
		return subPackagePattern;
	}

	public void setSubPackagePattern(String subPackagePattern) {
		this.subPackagePattern = subPackagePattern;
	}

	public String[] getRootPackages() {
		return rootPackages;
	}

	public void setRootPackages(String[] rootPackages) {
		this.rootPackages = rootPackages;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("PackagePatternDefinition [subPackagePattern=")
				.append(subPackagePattern).append(", rootPackages=")
				.append(Arrays.toString(rootPackages)).append("]");
		return builder.toString();
	}
}