package net.sf.extcos.util;

import static net.sf.extcos.util.StringUtils.SINGLE_SUBPACKAGE_PATTERN;
import static net.sf.extcos.util.StringUtils.append;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class ResourceUtils {
	/** URL prefix for loading from the file system: "file:" */
	public static final String FILE_URL_PREFIX = "file:";

	/** URL protocol for a file in the file system: "file" */
	public static final String URL_PROTOCOL_FILE = "file";

	/** URL protocol for an entry from a jar file: "jar" */
	public static final String URL_PROTOCOL_JAR = "jar";

	/** URL protocol for an entry from a zip file: "zip" */
	public static final String URL_PROTOCOL_ZIP = "zip";

	/** URL protocol for an entry from a JBoss jar file: "vfszip" */
	public static final String URL_PROTOCOL_VFSZIP = "vfszip";

	/** URL protocol for a JBoss VFS resource: "vfs" */
	public static final String URL_PROTOCOL_VFS = "vfs";

	/** URL protocol for an entry from a WebSphere jar file: "wsjar" */
	public static final String URL_PROTOCOL_WSJAR = "wsjar";

	/** URL protocol for an entry from an OC4J jar file: "code-source" */
	public static final String URL_PROTOCOL_CODE_SOURCE = "code-source";

	/** Separator between JAR URL and file path within the JAR */
	public static final String JAR_URL_SEPARATOR = "!/";

	private ResourceUtils() {
	} // so it can't be instantiated

	/**
	 * Determine whether the given URL points to a resource in a jar file, that
	 * is, has protocol "jar", "zip", "wsjar" or "code-source".
	 * <p>
	 * "zip" and "wsjar" are used by BEA WebLogic Server and IBM WebSphere,
	 * respectively, but can be treated like jar files. The same applies to
	 * "code-source" URLs on Oracle OC4J, provided that the path contains a jar
	 * separator.
	 * 
	 * @param url
	 *            the URL to check
	 * @return whether the URL has been identified as a JAR URL
	 */
	public static boolean isJarURL(URL url) {
		String protocol = url.getProtocol();
		return (URL_PROTOCOL_JAR.equals(protocol)
				|| URL_PROTOCOL_ZIP.equals(protocol)
				|| URL_PROTOCOL_WSJAR.equals(protocol) || (URL_PROTOCOL_CODE_SOURCE
				.equals(protocol) && url.getPath().indexOf(JAR_URL_SEPARATOR) != -1));
	}

	/**
	 * Determine whether the given URL points to a JBoss virtual file system
	 * (VFS) resource. This is the case if its protocol starts with
	 * <code>vfs</code>.
	 * 
	 * @param url
	 *            the URL to check
	 * @return whether the URL has been identified as a VFS URL
	 */
	public static boolean isVirtualFileSystemURL(URL url) {
		return url.getProtocol().startsWith(URL_PROTOCOL_VFS);
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>, i.e. to a
	 * file in the file system.
	 * 
	 * @param resourceUrl
	 *            the resource URL to resolve
	 * @return a corresponding File object
	 * @throws FileNotFoundException
	 *             if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URL resourceUrl) throws FileNotFoundException {
		return getFile(resourceUrl, append("URL [", resourceUrl, "]"));
	}

	/**
	 * Resolve the given resource URL to a <code>java.io.File</code>, i.e. to a
	 * file in the file system.
	 * 
	 * @param resourceUrl
	 *            the resource URL to resolve
	 * @param description
	 *            a description of the original resource that the URL was
	 *            created for (for example, a class path location)
	 * @return a corresponding File object
	 * @throws FileNotFoundException
	 *             if the URL cannot be resolved to a file in the file system
	 */
	public static File getFile(URL resourceUrl, String description)
			throws FileNotFoundException {
		Assert.notNull(resourceUrl, IllegalArgumentException.class,
				"Resource URL must not be null");
		if (!URL_PROTOCOL_FILE.equals(resourceUrl.getProtocol())) {
			throw new FileNotFoundException(append(description,
					" cannot be resolved to absolute file path ",
					"because it does not reside in the file system: ",
					resourceUrl));
		}
		try {
			return new File(toURI(resourceUrl).getSchemeSpecificPart());
		} catch (URISyntaxException ex) {
			// Fallback for URLs that are not valid URIs (should hardly ever
			// happen).
			return new File(resourceUrl.getFile());
		}
	}

	/**
	 * Create a URI instance for the given URL, replacing spaces with "%20"
	 * quotes first.
	 * <p>
	 * Furthermore, this method works on JDK 1.4 as well, in contrast to the
	 * <code>URL.toURI()</code> method.
	 * 
	 * @param url
	 *            the URL to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException
	 *             if the URL wasn't a valid URI
	 * @see java.net.URL#toURI()
	 */
	public static URI toURI(URL url) throws URISyntaxException {
		return toURI(url.toString());
	}

	/**
	 * Create a URI instance for the given location String, replacing spaces
	 * with "%20" quotes first.
	 * 
	 * @param location
	 *            the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException
	 *             if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(StringUtils.replace(location, " ", "%20"));
	}

	public static URL toURL(File file) throws MalformedURLException {
		return file.toURI().toURL();
	}

	public static boolean matches(URL rootDirectory, String basePath,
			List<String> subPathElements) {
		if (subPathElements.isEmpty()) {
			return true;
		} else {
			StringBuilder pattern = new StringBuilder(".*").append(basePath);
			
			for (String subPathElement : subPathElements) {
				if ("*".equals(subPathElement)) {
					pattern.append(SINGLE_SUBPACKAGE_PATTERN).append("/");
				} else if ("**".equals(subPathElement)) {
					pattern.append("(?:")
							.append(SINGLE_SUBPACKAGE_PATTERN)
							.append("/)+");
				} else {
					pattern.append(subPathElement).append("/");
				}
			}
			
			pattern.append(".*");
			
			return rootDirectory.toString().matches(pattern.toString());
		}
	}
}