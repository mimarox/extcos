package net.sf.extcos.selector.annotation;

import java.util.Set;

public interface ArgumentMappingDisjunction extends ArgumentMapping {
	Set<ArgumentMapping> getMappings();
}
