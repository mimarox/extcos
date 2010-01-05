package net.sf.extcos.internal;

import net.sf.extcos.selector.annotation.ArgumentKey;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentValue;
import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.util.Assert;

public class ArgumentMappingImpl implements ArgumentMapping {
	private String key;
	private ArgumentValue value;
	
	public ArgumentMappingImpl(ArgumentKey key, ArgumentValue value) {
		Assert.notNull(key, IllegalArgumentException.class);
		Assert.notNull(value, IllegalArgumentException.class);
		
		this.key = key.getKey();
		this.value = value;
	}

	public boolean isSetIn(AnnotationMetadata annotation) {
		if (annotation.hasKey(key)) {
			Object value = annotation.getValue(key);
			
			if (value != null) {
				return this.value.matches(value);
			}
		}
		
		return false;
	}
}