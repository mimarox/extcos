package net.sf.extcos.internal;

import java.util.Set;

import net.sf.extcos.filter.BlacklistManager;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.resource.Resource;

import com.google.inject.Inject;

public class BlacklistAwareResultSetProvider implements ResultSetProvider {
	
	@Inject
	private BlacklistManager blacklistManager;
	
	/*
	 * (non-Javadoc)
	 * @see org.jcs.filter.ResultSetProvider#getResultSet()
	 */
	public Set<Resource> getResultSet() {
		return blacklistManager.newResultSet();
	}
}