package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import net.sf.extcos.selector.annotation.ArgumentKey;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentMappingConjunction;
import net.sf.extcos.selector.annotation.ArgumentMappingDisjunction;
import net.sf.extcos.selector.annotation.ArgumentValue;
import net.sf.extcos.selector.annotation.ArgumentsDescriptor;
import net.sf.extcos.util.Assert;

public class ArgumentsDescriptorImpl implements ArgumentsDescriptor {
	private ArgumentMapping mapping;

	public ArgumentsDescriptorImpl(ArgumentKey key, ArgumentValue value) {
		Assert.notNull(key, iae());
		Assert.notNull(value, iae());
		mapping = new ArgumentMappingImpl(key, value);
	}

	public ArgumentsDescriptorImpl(ArgumentMapping mapping) {
		Assert.notNull(mapping, iae());
		this.mapping = mapping;		
	}

	public ArgumentsDescriptorImpl(ArgumentMappingConjunction mapping) {
		this((ArgumentMapping) mapping);
	}
	
	public ArgumentsDescriptorImpl(ArgumentMappingDisjunction mapping) {
		this((ArgumentMapping) mapping);
	}

	public ArgumentMapping getArgumentMapping() {
		return mapping;
	}
}