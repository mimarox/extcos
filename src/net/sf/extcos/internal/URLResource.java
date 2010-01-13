package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.extcos.classgeneration.ClassGenerationListener;
import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.spi.AnnotationMetadata;
import net.sf.extcos.spi.ClassGenerator;
import net.sf.extcos.spi.ResourceAccessor;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.Assert;

public class URLResource implements Resource {
	private URL resourceUrl;
	private ResourceType resourceType;
	private ResourceAccessor resourceAccessor;
	private ClassGenerator classGenerator;
	
	private Object inspectionMutex = new Object();
	private boolean inspected;
	
	private List<ClassGenerationListener> listeners = new ArrayList<ClassGenerationListener>();
	private Map<Class<? extends Annotation>, AnnotationMetadata> metadataCache = new HashMap<Class<? extends Annotation>, AnnotationMetadata>();
	
	public URLResource(ResourceType resourceType, URL resouceUrl) {
		Assert.notNull(resourceType, IllegalArgumentException.class);
		Assert.notNull(resouceUrl, IllegalArgumentException.class);

		this.resourceType = resourceType;
		this.resourceUrl = resouceUrl;
	}

	public AnnotationMetadata getAnnotationMetadata(
			Class<? extends Annotation> annotation)
			throws ConcurrentInspectionException {
		acquireLock();
		
		try {
			if (metadataCache.containsKey(annotation)) {
				return metadataCache.get(annotation);
			} else {
				AnnotationMetadata metadata =
					getResourceAccessor().getAnnotationMetadata(annotation);
				
				metadataCache.put(annotation, metadata);
				
				return metadata;
			}
		} finally {
			releaseLock();
		}
	}

	public void generateAndDispatchClass() {
		Class<?> clazz = getClassGenerator().generateClass();
		for (ClassGenerationListener listener : listeners) {
			listener.classGenerated(clazz);
		}
	}

	public boolean hasInterface(Class<?> interfaze)
			throws ConcurrentInspectionException {
		acquireLock();
		
		try {
			return getResourceAccessor().hasInterface(interfaze);
		} finally {
			releaseLock();
		}
	}

	public boolean isSubclassOf(Class<?> clazz)
			throws ConcurrentInspectionException {
		acquireLock();
		
		try {
			return getResourceAccessor().isSubclassOf(clazz);
		} finally {
			releaseLock();
		}
	}

	public String toString() {
		return append("URL [", resourceUrl, "]");
	}

	private ClassGenerator getClassGenerator() {
		if (classGenerator == null) {
			classGenerator = resourceType.getClassGenerator();
			classGenerator.setResourceUrl(resourceUrl);
		}
		return classGenerator;
	}

	private ResourceAccessor getResourceAccessor() {
		if (resourceAccessor == null) {
			resourceAccessor = resourceType.getResourceAccessor();
			resourceAccessor.setResourceUrl(resourceUrl);
		}
		return resourceAccessor;
	}

	public void addClassGenerationListener(ClassGenerationListener listener) {
		if (listener != null)
			listeners.add(listener);
	}
	
	private void acquireLock() throws ConcurrentInspectionException {
		synchronized (inspectionMutex) {
			if (inspected) {
				throw new ConcurrentInspectionException(toString());
			} else {
				inspected = true;
			}
		}
	}
	
	private void releaseLock() {
		synchronized (inspectionMutex) {
			inspected = false;
		}
	}

	public boolean isClass() {
		return getResourceAccessor().isClass();
	}
}