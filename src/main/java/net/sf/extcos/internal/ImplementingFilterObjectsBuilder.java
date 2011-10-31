package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.Set;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class ImplementingFilterObjectsBuilder extends AbstractFilterObjectsBuilder {
	private final ResultSetProvider provider = new StandardResultSetProvider();

	@Override
	public void buildFilterObjects(final TypeFilter filter, Connector connector) {
		Assert.notNull(filter, iae());
		Assert.isTrue(filter instanceof ImplementingTypeFilter, iae());
		Assert.notNull(connector, iae());

		Set<Class<?>> interfaces = ((ImplementingTypeFilter) filter).getInterfaces();

		if (interfaces.size() > 1) {
			ConjunctiveChainedConnector conjunction = new ConjunctiveChainedConnector();
			conjunction.setParentConnector(connector);
			conjunction.setChildCount(interfaces.size());
			connector = conjunction;
		}

		for (Class<?> interfaze : interfaces) {
			ImplementingResourceMatcher matcher = new ImplementingResourceMatcher(interfaze);

			if (buildContext.isRegistered(matcher)) {
				FilterObjects fo = buildContext.getFilterObjects(matcher);
				fo.getResourceDispatcher().addConnector(connector);
			} else {
				MultiplexingConnector dispatcher =
						new StandardMultiplexingConnector();

				dispatcher.addConnector(connector);

				FilterObjects filterObjects =
						createFilterObjects(dispatcher, matcher, provider);

				buildContext.register(filterObjects);
			}
		}

		if (connector instanceof ConjunctiveChainedConnector) {
			buildContext.register(filter,
					(ConjunctiveChainedConnector)connector);
		}
	}
}