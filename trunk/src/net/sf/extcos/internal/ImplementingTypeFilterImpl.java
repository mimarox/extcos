package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.util.Assert;

public class ImplementingTypeFilterImpl implements ImplementingTypeFilter {
	private Set<Class<?>> interfaces;
	
	public ImplementingTypeFilterImpl(Class<?>... interfaces) {
		Assert.notEmpty(interfaces, iae());
		
		this.interfaces = new HashSet<Class<?>>();
		
		for (Class<?> interfaze : interfaces) {
			Assert.isTrue(Modifier.isInterface(interfaze.getModifiers()), iae());
			this.interfaces.add(interfaze);
		}
	}
	
	@Override
    public Set<Class<?>> getInterfaces() {
	    return interfaces;
	}
}