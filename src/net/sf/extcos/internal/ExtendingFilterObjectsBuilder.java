package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;

public class ExtendingFilterObjectsBuilder extends AbstractFilterObjectsBuilder {
	
	@Inject
	@Named("efob.provider")
	private ResultSetProvider provider;
	
	@Inject
	private Injector injector;
	
	public void buildFilterObjects(TypeFilter filter, Connector connector) {
		Assert.notNull(filter, iae());
		Assert.isTrue(filter instanceof ExtendingTypeFilter, iae());
		Assert.notNull(connector, iae());
		
		ExtendingResourceMatcher matcher = new ExtendingResourceMatcher(
				((ExtendingTypeFilter) filter).getClazz());
		
		if (buildContext.isRegistered(matcher)) {
			FilterObjects fo = buildContext.getFilterObjects(matcher);
			fo.getResourceDispatcher().addConnector(connector);
		} else {
			MultiplexingConnector dispatcher =
				new BlacklistAwareMultiplexingConnector();
			injector.injectMembers(dispatcher);
			
			dispatcher.addConnector(connector);
			
			FilterObjects filterObjects =
				createFilterObjects(dispatcher, matcher, provider);
			
			buildContext.register(filter, dispatcher);
			buildContext.register(filterObjects);
		}
	}
}