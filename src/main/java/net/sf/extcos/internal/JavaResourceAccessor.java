package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;
import java.net.URL;

import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.spi.ResourceAccessor;
import net.sf.extcos.util.Assert;

public class JavaResourceAccessor implements ResourceAccessor {
	private URL resourceUrl;	
	
	@Override
	public AnnotationMetadata getAnnotationMetadata(
			Class<? extends Annotation> annotation) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasInterface(Class<?> interfaze) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClass() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubclassOf(Class<?> clazz) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setResourceUrl(URL resourceUrl) {
        Assert.notNull(resourceUrl, iae());
        this.resourceUrl = resourceUrl;
	}
}
