package net.sf.extcos.selector.annotation;

import java.util.Set;

public interface ArgumentMappingJunction extends ArgumentMapping {
	Set<ArgumentMapping> getMappings();
}
