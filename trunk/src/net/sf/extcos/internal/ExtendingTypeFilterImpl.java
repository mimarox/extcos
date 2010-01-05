package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.reflect.Modifier;

import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.util.Assert;

public class ExtendingTypeFilterImpl implements ExtendingTypeFilter {
	private Class<?> clazz;
	
	public ExtendingTypeFilterImpl(Class<?> clazz) {
		Assert.notNull(clazz, iae());
		
		int modifiers = clazz.getModifiers();
		Assert.isFalse(Modifier.isInterface(modifiers), iae());
		Assert.isFalse(Modifier.isFinal(modifiers), iae());
		
		this.clazz = clazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}
}