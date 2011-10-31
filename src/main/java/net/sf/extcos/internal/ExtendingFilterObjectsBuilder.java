package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class ExtendingFilterObjectsBuilder extends AbstractFilterObjectsBuilder {
	private final ResultSetProvider provider = new BlacklistAwareResultSetProvider();

	@Override
	public void buildFilterObjects(final TypeFilter filter, final Connector connector) {
		Assert.notNull(filter, iae());
		Assert.isTrue(filter instanceof ExtendingTypeFilter, iae());
		Assert.notNull(connector, iae());

		ExtendingResourceMatcher matcher = new ExtendingResourceMatcher(
				((ExtendingTypeFilter) filter).getClazz());

		if (buildContext.isRegistered(matcher)) {
			FilterObjects fo = buildContext.getFilterObjects(matcher);
			fo.getResourceDispatcher().addConnector(connector);
		} else {
			MultiplexingConnector dispatcher = new BlacklistAwareMultiplexingConnector();
			dispatcher.addConnector(connector);

			FilterObjects filterObjects = createFilterObjects(dispatcher, matcher, provider);

			buildContext.register(filter, dispatcher);
			buildContext.register(filterObjects);
		}
	}
}