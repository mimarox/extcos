package net.sf.extcos.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentMappingConjunction;
import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.util.Assert;

public class ArgumentMappingConjunctionImpl implements
		ArgumentMappingConjunction {
	private Set<ArgumentMapping> mappings;
	
	public ArgumentMappingConjunctionImpl(ArgumentMapping... mappings) {
		Assert.notEmpty(mappings, IllegalArgumentException.class);

		this.mappings = new HashSet<ArgumentMapping>(
				Arrays.asList(mappings));
	}

	public boolean isSetIn(AnnotationMetadata annotation) {
		for (ArgumentMapping mapping : mappings) {
			if (!mapping.isSetIn(annotation))
				return false;
		}
		
		return true;
	}

	@Override
	public Set<ArgumentMapping> getMappings() {
		return mappings;
	}
}