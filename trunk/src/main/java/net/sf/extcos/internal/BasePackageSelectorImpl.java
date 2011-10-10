package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.selector.BasePackageSelector;
import net.sf.extcos.selector.ForwardingBuilder;
import net.sf.extcos.selector.Package;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class BasePackageSelectorImpl implements BasePackageSelector {

	@Inject
	@Named("bpsi.basePackages")
	private Set<Package> basePackages;

	@Inject
	private ForwardingBuilder forwardingBuilder;

	@Override
	public ForwardingBuilder from(@SuppressWarnings("hiding") final String... basePackages) {
		Assert.notEmpty(basePackages, IllegalArgumentException.class,
				"there must be at least one basePackage set");

		for (String basePackage : basePackages) {
			this.basePackages.add(new PackageImpl(basePackage));
		}

		return forwardingBuilder;
	}

	@Override
	public Set<Package> getBasePackages() {
		return basePackages;
	}

	@Override
	public ForwardingBuilder getForwardingBuilder() {
		return forwardingBuilder;
	}
}