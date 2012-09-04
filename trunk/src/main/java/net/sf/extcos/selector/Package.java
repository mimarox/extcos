package net.sf.extcos.selector;

import static net.sf.extcos.util.StringUtils.SINGLE_SUBPACKAGE_PATTERN;
import static net.sf.extcos.util.StringUtils.append;
import static net.sf.extcos.util.StringUtils.isJavaPackageWithWildcards;
import net.sf.extcos.util.Assert;

public class Package {
	private final String name;
	private final String basePath;
	private final String subPathPattern;
	
	public Package(final String name) {
		Assert.notNull(name, IllegalArgumentException.class);
		Assert.isTrue(isJavaPackageWithWildcards(name) && !name.startsWith("*"),
				IllegalArgumentException.class,
				append(name, " is not a valid base package name"));
		
		this.name = name;
		this.basePath = determineBasePath(this.name);
		this.subPathPattern = determineSubPathPattern(normalizeName(this.name), basePath);
	}
	
	private String normalizeName(String name) {		
		for (String suffix : new String[]{".*", ".**"})
			if (name.endsWith(suffix))
				return name.substring(0, name.length() - suffix.length());
		
		return name;
	}

	private String determineBasePath(String name) {
		String[] nameParts = name.split("\\.");
		StringBuilder basePath = new StringBuilder();
		
		for (String namePart : nameParts) {
			if (!namePart.startsWith("*")) {
				basePath.append(namePart).append("/");
			} else {
				break;
			}
		}
		
		return basePath.toString();
	}

	private String determineSubPathPattern(String name, String basePath) {
		if (name.length() <= basePath.length()) {
			return ".*";
		}
		
		String subPath = name.substring(basePath.length());
		StringBuilder pattern = new StringBuilder();
		
		for (String subPathElement : subPath.split("\\.")) {
			if ("*".equals(subPathElement)) {
				pattern.append(SINGLE_SUBPACKAGE_PATTERN).append("/");
			} else if ("**".equals(subPathElement)) {
				pattern.append("(?:")
						.append(SINGLE_SUBPACKAGE_PATTERN)
						.append("/)+");
			} else {
				pattern.append(subPathElement).append("/");
			}
		}
		
		return pattern.toString();
	}

	public String getName() {
		return name;
	}

	public String getBasePath() {
		return basePath;
	}
	
	public String getSubPathPattern() {
		return subPathPattern;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Package [name=").append(name).append("]");
		return builder.toString();
	}
}