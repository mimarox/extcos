package net.sf.extcos.internal;

import net.sf.extcos.selector.annotation.ArgumentKey;
import net.sf.extcos.util.Assert;

public class ArgumentKeyImpl implements ArgumentKey {
	private String key;
	
	public ArgumentKeyImpl(String key) {
		Assert.notEmpty(key, IllegalArgumentException.class);
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}