package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.resource.Resource;

public class BlacklistAwareResultSetProvider implements ResultSetProvider {
	private final BlacklistManager blacklistManager = BlacklistManager.getInstance();

	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResultSetProvider#getResultSet()
	 */
	@Override
	public Set<Resource> getResultSet() {
		return blacklistManager.newResultSet();
	}
}