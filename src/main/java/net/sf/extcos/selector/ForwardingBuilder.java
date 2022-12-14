package net.sf.extcos.selector;

import static net.sf.extcos.util.Assert.iae;

import java.net.URL;
import java.util.Set;

import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.internal.EnumBasedReturning;
import net.sf.extcos.internal.Returning;
import net.sf.extcos.util.Assert;

public class ForwardingBuilder {
	private final Set<StoreBinding> storeBindings = new ArraySet<StoreBinding>();
	private final ReturningSelector returningSelector = new ReturningSelector();
	private DirectReturning returning = new EnumBasedReturning(Returning.ALL);
	private URL[] rootDirectories;
	
	private boolean calledWithin;
	private boolean calledAndStore;
	private boolean calledReturning;

	public ForwardingBuilder within(URL... rootDirectories) {
		Assert.state(!calledWithin);
		
		this.calledWithin = true;
		this.rootDirectories = rootDirectories;
		
		return this;
	}
	
	public ReturningSelector andStore(final StoreBinding... bindings) {
		Assert.state(firstEntry());
		Assert.notEmpty(bindings, iae());

		for (StoreBinding storeBinding : bindings) {
			this.storeBindings.add(storeBinding);
		}

		calledAndStore = true;
		return returningSelector;
	}

	public void returning(@SuppressWarnings("hiding") final DirectReturning returning) {
		Assert.state(firstEntry());
		Assert.notNull(returning, iae());

		this.returning = returning;
		calledReturning = true;
	}

	public URL[] getRootDirectories() {
		return rootDirectories;
	}
	
	public Set<StoreBinding> getStoreBindings() {
		return storeBindings;
	}

	public StoreReturning getReturning() {
		if (calledAndStore) {
			return returningSelector.getStoreReturning();
		}

		return returning;
	}

	private boolean firstEntry() {
		return !calledAndStore && !calledReturning;
	}
}