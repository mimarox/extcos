package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import net.sf.extcos.selector.ReturningSelector;
import net.sf.extcos.selector.StoreReturning;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;

public class ReturningSelectorImpl implements ReturningSelector {
	
	@Inject
	private StoreReturning returning;
	
	public void returning(StoreReturning returning) {
		Assert.notNull(returning, iae());
		this.returning = returning;
	}
	
	public StoreReturning getStoreReturning() {
		return returning;
	}
}