package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.Set;

import net.sf.extcos.filter.ChainedConnector;
import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.builder.BuildSupport;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypeFilterJunction;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;

public abstract class AbstractFilterObjectsJunctionBuilder extends
AbstractFilterObjectsBuilder {

	@Inject
	private BuildSupport buildSupport;

	@Override
	public void buildFilterObjects(final TypeFilter filter, final Connector parent) {
		Assert.notNull(filter, iae());
		Assert.isTrue(filter instanceof TypeFilterJunction, iae());
		Assert.notNull(parent, iae());

		if (!(parent instanceof ImmediateConnector) &&
				buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(parent);
		} else {
			Set<TypeFilter> children =
					((TypeFilterJunction) filter).getTypeFilters();

			ChainedConnector connector = getConnector();

			connector.setChildCount(children.size());
			connector.setParentConnector(parent);

			for (TypeFilter child : children) {
				buildSupport.buildFilterObjects(child, connector);
			}

			buildContext.register(filter, connector);
		}
	}

	protected abstract ChainedConnector getConnector();
}