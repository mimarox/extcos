package net.sf.extcos.selector;

import java.util.Set;

public interface BasePackageSelector {

	ForwardingBuilder from(String... basePackages);
	
	Set<Package> getBasePackages();
	
	ForwardingBuilder getForwardingBuilder();
}