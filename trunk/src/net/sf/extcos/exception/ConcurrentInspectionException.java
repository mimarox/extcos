package net.sf.extcos.exception;

import static net.sf.extcos.util.StringUtils.append;

/**
 * Exception that is to be thrown if one {@link Thread} tries to inspect a
 * {@link net.sf.extcos.resource.Resource Resource} whilst another Thread is already inspecting this
 * resource.
 * 
 * @author Matthias Rothe
 */
public class ConcurrentInspectionException extends Exception {
	private static final long serialVersionUID = -2144555716995813758L;

	public ConcurrentInspectionException(String resource) {
		super(append("inspection denied for resource ", resource));
	}
}