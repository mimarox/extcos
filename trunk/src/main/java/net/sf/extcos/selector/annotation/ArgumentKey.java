package net.sf.extcos.selector.annotation;

import net.sf.extcos.util.Assert;

public class ArgumentKey {
	private final String key;

	public ArgumentKey(final String key) {
		Assert.notEmpty(key, IllegalArgumentException.class);
		this.key = key;
	}

	public String getKey() {
		return key;
	}
}