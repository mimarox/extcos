package net.sf.extcos.internal;

import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.MatchingChainedFilter;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.filter.builder.BuildContext;
import net.sf.extcos.filter.builder.FilterObjectsBuilder;

import com.google.inject.Inject;
import com.google.inject.Injector;

public abstract class AbstractFilterObjectsBuilder implements
		FilterObjectsBuilder {

	@Inject
	protected BuildContext buildContext;
	
	@Inject
	protected Injector injector;
	
	protected FilterObjects createFilterObjects(
			MultiplexingConnector dispatcher, ResourceMatcher matcher,
			ResultSetProvider provider) {
		FilterObjects filterObjects = injector.getInstance(FilterObjects.class);

		filterObjects.setFilter(injector.getInstance(MatchingChainedFilter.class));
		filterObjects.setResourceDispatcher(dispatcher);
		filterObjects.setResourceMatcher(matcher);
		filterObjects.setResultSetProvider(provider);

		return filterObjects;
	}
}