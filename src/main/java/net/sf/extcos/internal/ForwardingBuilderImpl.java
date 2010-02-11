package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.Set;

import net.sf.extcos.selector.DirectReturning;
import net.sf.extcos.selector.ForwardingBuilder;
import net.sf.extcos.selector.ReturningSelector;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ForwardingBuilderImpl implements ForwardingBuilder {
	
	@Inject
	private ReturningSelector returningSelector;
	
	@Inject
	@Named("fbi.storeBindings")
	private Set<StoreBinding> storeBindings;
	
	@Inject
    private DirectReturning returning;
    
	private boolean calledAndStore;
	private boolean calledReturning;
	
	public ReturningSelector andStore(StoreBinding... bindings) {
		Assert.state(firstEntry());
		Assert.notEmpty(bindings, iae());
		
		for (StoreBinding storeBinding : bindings) {
			this.storeBindings.add(storeBinding);
		}
		
		calledAndStore = true;
		return returningSelector;
	}

	public void returning(DirectReturning returning) {
		Assert.state(firstEntry());
		Assert.notNull(returning, iae());
		
		this.returning = returning;
		calledReturning = true;
	}
	
	public Set<StoreBinding> getStoreBindings() {
		return storeBindings;
	}
	
	public StoreReturning getReturning() {
		if (calledAndStore) {
			return returningSelector.getStoreReturning();
		} else {
			return returning;
		}
	}
	
	private boolean firstEntry() {
		return !calledAndStore && !calledReturning;
	}
}