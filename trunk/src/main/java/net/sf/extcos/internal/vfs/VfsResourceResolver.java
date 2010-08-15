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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VfsResourceResolver implements InvocationHandler {
	private static Log logger = LogFactory.getLog(VfsResourceResolver.class);
	
	private String rootPath;
	private Set<ResourceType> resourceTypes;
	private Set<Resource> resources = new LinkedHashSet<Resource>();
	
	public Set<Resource> findResources(Set<ResourceType> resourceTypes,
			URL rootDirectory) throws IOException {
		Object root = VfsUtils.getRoot(rootDirectory);
		setRootPath(VfsUtils.getPath(root));
		this.resourceTypes = resourceTypes;
		
		VfsUtils.visit(root, this);
		return resources;
	}

	private void setRootPath(String path) {
		this.rootPath = (path.length() == 0 || path.endsWith("/") ? path : path + "/");
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		String methodName = method.getName();
		if (Object.class.equals(method.getDeclaringClass())) {
			if (methodName.equals("equals")) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
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

	private void visit(Object vfsResource) {
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
	
	private String getResourcePath(Object vfsResource) {
		return VfsUtils.getPath(vfsResource).substring(rootPath.length());
	}
}