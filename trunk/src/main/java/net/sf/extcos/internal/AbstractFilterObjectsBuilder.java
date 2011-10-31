package net.sf.extcos.internal;

import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.filter.builder.FilterObjectsBuilder;

public abstract class AbstractFilterObjectsBuilder implements
FilterObjectsBuilder {

	protected BuildContext buildContext = BuildContext.getInstance();

	protected FilterObjects createFilterObjects(
			final MultiplexingConnector dispatcher, final ResourceMatcher matcher,
			final ResultSetProvider provider) {
		FilterObjects filterObjects = new FilterObjects();

		filterObjects.setFilter(new MatchingChainedFilterImpl());
		filterObjects.setResourceDispatcher(dispatcher);
		filterObjects.setResourceMatcher(matcher);
		filterObjects.setResultSetProvider(provider);

		return filterObjects;
	}
}