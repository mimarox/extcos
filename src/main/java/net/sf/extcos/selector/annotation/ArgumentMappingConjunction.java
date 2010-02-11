package net.sf.extcos.selector.annotation;

import java.util.Set;

public interface ArgumentMappingConjunction extends ArgumentMapping {
	Set<ArgumentMapping> getMappings();
}
