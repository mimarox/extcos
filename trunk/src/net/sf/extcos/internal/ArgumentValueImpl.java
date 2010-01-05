package net.sf.extcos.internal;

import net.sf.extcos.selector.annotation.ArgumentValue;
import net.sf.extcos.util.Assert;

public class ArgumentValueImpl implements ArgumentValue {
	private Object value;
	
	public ArgumentValueImpl(Object value) {
		Assert.notNull(value, IllegalArgumentException.class);
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}

	public boolean matches(Object value) {
		return this.value.equals(value);
	}
}