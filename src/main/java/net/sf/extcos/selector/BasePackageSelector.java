package net.sf.extcos.selector;

import java.util.Set;

import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.util.Assert;

public class BasePackageSelector {
	private final Set<Package> basePackages = new ArraySet<Package>();
	private final ForwardingBuilder forwardingBuilder = new ForwardingBuilder();

	public ForwardingBuilder from(@SuppressWarnings("hiding") final String... basePackages) {
		Assert.notEmpty(basePackages, IllegalArgumentException.class,
				"there must be at least one basePackage set");

		for (String basePackage : basePackages) {
			this.basePackages.add(new Package(basePackage));
		}

		return forwardingBuilder;
	}

	public Set<Package> getBasePackages() {
		return basePackages;
	}

	public ForwardingBuilder getForwardingBuilder() {
		return forwardingBuilder;
	}
}