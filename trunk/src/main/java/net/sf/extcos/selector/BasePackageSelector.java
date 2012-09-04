package net.sf.extcos.selector;

import java.util.Set;

import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.util.Assert;

public class BasePackageSelector {
	private final Set<Package> basePackages = new ArraySet<Package>();
	private final ForwardingBuilder forwardingBuilder = new ForwardingBuilder();
	private Boolean includingEnums;

	public ForwardingBuilder from(@SuppressWarnings("hiding") final String... basePackages) {
		Assert.notEmpty(basePackages, IllegalArgumentException.class,
				"there must be at least one basePackage set");

		for (String basePackage : basePackages) {
			this.basePackages.add(new Package(basePackage));
		}

		if (includingEnums == null) {
			includingEnums = false;
		}
		
		return forwardingBuilder;
	}
	
	public BasePackageSelector includingEnums() {
		if (includingEnums == null) {
			includingEnums = true;
			return this;
		}
		
		throw new IllegalStateException("includingEnums must not be called more than once!");
	}

	public Set<Package> getBasePackages() {
		return basePackages;
	}

	public ForwardingBuilder getForwardingBuilder() {
		return forwardingBuilder;
	}
	
	public boolean isIncludingEnums() {
		return includingEnums != null && includingEnums;
	}
}