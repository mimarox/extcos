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
import net.sf.extcos.spi.ResourceAccessor;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.Assert;

public class URLResource implements Resource {
	private final URL resourceUrl;
	private final ResourceType resourceType;
	private ResourceAccessor resourceAccessor;

	private final Object inspectionMutex = new Object();
	private boolean inspected;

	private final List<ClassGenerationListener> listeners = new ArrayList<ClassGenerationListener>();
	private final Map<Class<? extends Annotation>, AnnotationMetadata> metadataCache = new HashMap<Class<? extends Annotation>, AnnotationMetadata>();

	public URLResource(final ResourceType resourceType, final URL resouceUrl) {
		Assert.notNull(resourceType, IllegalArgumentException.class);
		Assert.notNull(resouceUrl, IllegalArgumentException.class);

		this.resourceType = resourceType;
		this.resourceUrl = resouceUrl;
	}

	@Override
	public AnnotationMetadata getAnnotationMetadata(
			final Class<? extends Annotation> annotation)
					throws ConcurrentInspectionException {
		acquireLock();

		try {
			if (metadataCache.containsKey(annotation)) {
				return metadataCache.get(annotation);
			}

			AnnotationMetadata metadata = getResourceAccessor().getAnnotationMetadata(annotation);
			metadataCache.put(annotation, metadata);
			return metadata;
		} finally {
			releaseLock();
		}
	}

	@Override
	public void generateAndDispatchClass() {
		Class<?> clazz = getResourceAccessor().generateClass();
		for (ClassGenerationListener listener : listeners) {
			listener.classGenerated(clazz);
		}
	}

	@Override
	public boolean hasInterface(final Class<?> interfaze)
			throws ConcurrentInspectionException {
		acquireLock();

		try {
			return getResourceAccessor().hasInterface(interfaze);
		} finally {
			releaseLock();
		}
	}

	@Override
	public boolean isSubclassOf(final Class<?> clazz)
			throws ConcurrentInspectionException {
		acquireLock();

		try {
			return getResourceAccessor().isSubclassOf(clazz);
		} finally {
			releaseLock();
		}
	}

	private ResourceAccessor getResourceAccessor() {
		if (resourceAccessor == null) {
			resourceAccessor = resourceType.getResourceAccessor();
			resourceAccessor.setResourceUrl(resourceUrl);
		}
		return resourceAccessor;
	}

	@Override
	public void addClassGenerationListener(final ClassGenerationListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	private void acquireLock() throws ConcurrentInspectionException {
		synchronized (inspectionMutex) {
			if (inspected) {
				throw new ConcurrentInspectionException(toString());
			}

			inspected = true;
		}
	}

	private void releaseLock() {
		synchronized (inspectionMutex) {
			inspected = false;
		}
	}

	@Override
	public boolean isClass() throws ConcurrentInspectionException {
		acquireLock();

		try {
			return getResourceAccessor().isClass();
		} finally {
			releaseLock();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resourceUrl == null) ? 0 : resourceUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		URLResource other = (URLResource) obj;
		if (resourceUrl == null) {
			if (other.resourceUrl != null) {
				return false;
			}
		} else if (!resourceUrl.equals(other.resourceUrl)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return append("URL [", resourceUrl, "]");
	}
}