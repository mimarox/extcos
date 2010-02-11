package net.sf.extcos.internal;

import net.sf.extcos.selector.DirectReturning;
import net.sf.extcos.util.Assert;

public class EnumBasedReturning implements DirectReturning {
	private Returning returning;
	
	public EnumBasedReturning(Returning returning) {
		Assert.notNull(returning, IllegalArgumentException.class);
		this.returning = returning;
	}
	
	public Returning getReturningType() {
		return returning;
	}
}