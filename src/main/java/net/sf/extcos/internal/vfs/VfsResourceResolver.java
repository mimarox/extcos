package net.sf.extcos.internal.vfs;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.extcos.internal.URLResource;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.spi.ResourceType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VfsResourceResolver implements InvocationHandler {
	private static Logger logger = LoggerFactory.getLogger(VfsResourceResolver.class);

	private String rootPath;
	private Set<ResourceType> resourceTypes;
	private final Set<Resource> resources = new LinkedHashSet<Resource>();

	@SuppressWarnings("hiding")
	public Set<Resource> findResources(final Set<ResourceType> resourceTypes,
			final URL rootDirectory) throws IOException {
		Object root = VfsUtils.getRoot(rootDirectory);
		setRootPath(VfsUtils.getPath(root));
		this.resourceTypes = resourceTypes;

		VfsUtils.visit(root, this);
		return resources;
	}

	private void setRootPath(final String path) {
		this.rootPath = path.length() == 0 || path.endsWith("/") ? path : path + "/";
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if (Object.class.equals(method.getDeclaringClass())) {
			if (methodName.equals("equals")) {
				// Only consider equal when proxies are identical.
				return proxy == args[0];
			}
			else if (methodName.equals("hashCode")) {
				return System.identityHashCode(proxy);
			}
		}
		else if ("getAttributes".equals(methodName)) {
			return getAttributes();
		}
		else if ("visit".equals(methodName)) {
			visit(args[0]);
			return null;
		}
		else if ("toString".equals(methodName)) {
			return toString();
		}

		throw new IllegalStateException("Unexpected method invocation: " + method);
	}

	private Object getAttributes() {
		return VfsUtils.getVisitorAttribute();
	}

	private void visit(final Object vfsResource) {
		String resourcePath = getResourcePath(vfsResource);

		for (ResourceType resourceType : resourceTypes) {
			if (resourcePath.endsWith(resourceType.getFileSuffix())) {
				try {
					URL resourceUrl = VfsUtils.getURL(vfsResource);
					resources.add(new URLResource(resourceType, resourceUrl));
				} catch (IOException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("couldn't resolve the URL for " +
								vfsResource, e);
					}
				}
			}
		}
	}

	private String getResourcePath(final Object vfsResource) {
		return VfsUtils.getPath(vfsResource).substring(rootPath.length());
	}
}
