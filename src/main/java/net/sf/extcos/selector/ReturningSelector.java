package net.sf.extcos.selector;

import static net.sf.extcos.util.Assert.iae;
import net.sf.extcos.internal.EnumBasedReturning;
import net.sf.extcos.internal.Returning;
import net.sf.extcos.util.Assert;

public class ReturningSelector {
	private StoreReturning returning = new EnumBasedReturning(Returning.NONE);

	public void returning(@SuppressWarnings("hiding") final StoreReturning returning) {
		Assert.notNull(returning, iae());
		this.returning = returning;
	}

	public StoreReturning getStoreReturning() {
		return returning;
	}
}