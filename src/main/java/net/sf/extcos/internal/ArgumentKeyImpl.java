package net.sf.extcos.internal;

import net.sf.extcos.selector.annotation.ArgumentKey;
import net.sf.extcos.util.Assert;

public class ArgumentKeyImpl implements ArgumentKey {
	private final String key;

	public ArgumentKeyImpl(final String key) {
		Assert.notEmpty(key, IllegalArgumentException.class);
		this.key = key;
	}

	@Override
	public String getKey() {
		return key;
	}
}