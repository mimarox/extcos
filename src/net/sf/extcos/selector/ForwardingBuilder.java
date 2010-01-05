package net.sf.extcos.selector;

import java.util.Set;

public interface ForwardingBuilder {
	ReturningSelector andStore(StoreBinding... bindings);
	void returning(DirectReturning returning);
	public abstract StoreReturning getReturning();
	public abstract Set<StoreBinding> getStoreBindings();
}