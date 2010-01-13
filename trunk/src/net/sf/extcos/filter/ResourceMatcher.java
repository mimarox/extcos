package net.sf.extcos.filter;

import net.sf.extcos.exception.ConcurrentInspectionException;
import net.sf.extcos.resource.Resource;

public interface ResourceMatcher {
	
	/**
	 * Determines whether a given {@link Resource} matches this ResourceMatcher.
	 * Throws an {@link ConcurrentInspectionException} if the resource is
	 * already being inspected.
	 * 
	 * @param resource
	 *            The resource to match
	 * @return true, if and only if the given resources matches, false otherwise
	 * @throws ConcurrentInspectionException
	 *             if the given resource is already being inspected
	 */
	boolean matches(Resource resource) throws ConcurrentInspectionException;

	/**
	 * Checks if this matcher's {@link #matches(Resource)} method matches
	 * {@link Resource Resources} against the given object.
	 * 
	 * @param obj
	 *            The onject
	 * @return true, if and only if this matcher 's {@link #matches(Resource)}
	 *         method matches {@link Resource Resources} against the given
	 *         object, false otherwise
	 */
	boolean isMatcherFor(Object obj);
	
	int hashCode();
	
	boolean equals(Object obj);
}