package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;
import java.util.Set;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.annotation.AnnotationArgument;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentMappingConjunction;
import net.sf.extcos.selector.annotation.ArgumentMappingJunction;
import net.sf.extcos.selector.annotation.ArgumentsDescriptor;
import net.sf.extcos.util.Assert;

public class AnnotatedWithFilterObjectsBuilder extends AbstractFilterObjectsBuilder {
	private final ResultSetProvider provider = new StandardResultSetProvider();

	@Override
	public void buildFilterObjects(final TypeFilter filter, final Connector connector) {
		Assert.notNull(filter, iae());
		Assert.isTrue(filter instanceof AnnotatedWithTypeFilter, iae());
		Assert.notNull(connector, iae());

		AnnotatedWithTypeFilter awtf = (AnnotatedWithTypeFilter) filter;
		ArgumentsDescriptor arguments = awtf.getArguments();

		if (arguments == null) {
			buildFilterObjects(awtf.getAnnotation(), connector);
		} else {
			buildFilterObjects(awtf.getAnnotation(), arguments, connector);
		}
	}

	private void buildFilterObjects(final Class<? extends Annotation> annotation,
			final Connector connector) {
		AnnotatedWithResourceMatcher matcher =
				new AnnotatedWithResourceMatcher(annotation);

		if (buildContext.isRegistered(matcher)) {
			FilterObjects fo = buildContext.getFilterObjects(matcher);
			fo.getResourceDispatcher().addConnector(connector);
		} else {
			MultiplexingConnector dispatcher =
					new StandardMultiplexingConnector();

			dispatcher.addConnector(connector);

			FilterObjects filterObjects =
					createFilterObjects(dispatcher, matcher, provider);

			TypeFilter filter =
					new AnnotatedWithTypeFilterImpl(annotation, null);

			buildContext.register(filter, dispatcher);
			buildContext.register(filterObjects);
		}
	}

	private void buildFilterObjects(final Class<? extends Annotation> annotation,
			final ArgumentsDescriptor arguments, final Connector connector) {
		ConjunctiveChainedConnector conjunction = new ConjunctiveChainedConnector();
		conjunction.setParentConnector(connector);
		conjunction.setChildCount(2);

		buildFilterObjects(annotation, conjunction);

		ArgumentMapping mapping = arguments.getArgumentMapping();
		if (mapping instanceof ArgumentMappingConjunction) {
			buildFilterObjects(annotation, (ArgumentMappingConjunction) mapping, conjunction);
		} else if (mapping instanceof ArgumentMappingJunction) {
			buildFilterObjects(annotation, (ArgumentMappingJunction) mapping, conjunction);
		} else {
			buildFilterObjects(annotation, mapping, connector);
		}
	}

	private void buildFilterObjects(final Class<? extends Annotation> annotation,
			final ArgumentMapping mapping, final Connector connector) {
		TypeFilter filter =
				new AnnotatedWithTypeFilterImpl(annotation, new ArgumentsDescriptor(mapping));

		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
			return;
		}

		AnnotationArgumentResourceMatcher matcher = new AnnotationArgumentResourceMatcher(
				new AnnotationArgument(annotation, mapping));

		if (buildContext.isRegistered(matcher)) {
			FilterObjects fo = buildContext.getFilterObjects(matcher);
			fo.getResourceDispatcher().addConnector(connector);
		} else {
			MultiplexingConnector dispatcher =
					new StandardMultiplexingConnector();

			dispatcher.addConnector(connector);

			FilterObjects filterObjects =
					createFilterObjects(dispatcher, matcher, provider);

			buildContext.register(filter, dispatcher);
			buildContext.register(filterObjects);
		}
	}

	private void buildFilterObjects(final Class<? extends Annotation> annotation,
			final ArgumentMappingConjunction mappingConjunction, final Connector connector) {
		TypeFilter filter =	new AnnotatedWithTypeFilterImpl(annotation,
				new ArgumentsDescriptor(mappingConjunction));

		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
			return;
		}

		Set<ArgumentMapping> mappings = mappingConjunction.getMappings();

		ConjunctiveChainedConnector conjunction = new ConjunctiveChainedConnector();
		conjunction.setParentConnector(connector);
		conjunction.setChildCount(mappings.size());

		for (ArgumentMapping mapping : mappings) {
			if (mapping instanceof ArgumentMappingConjunction) {
				buildFilterObjects(annotation, (ArgumentMappingConjunction) mapping, conjunction);
			} else if (mapping instanceof ArgumentMappingJunction) {
				buildFilterObjects(annotation, (ArgumentMappingJunction) mapping, conjunction);
			} else {
				buildFilterObjects(annotation, mapping, connector);
			}
		}

		buildContext.register(filter, conjunction);
	}

	private void buildFilterObjects(final Class<? extends Annotation> annotation,
			final ArgumentMappingJunction mappingDisjunction, final Connector connector) {
		TypeFilter filter =	new AnnotatedWithTypeFilterImpl(annotation,
				new ArgumentsDescriptor(mappingDisjunction));

		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
			return;
		}

		Set<ArgumentMapping> mappings = mappingDisjunction.getMappings();

		DisjunctiveChainedConnector disjunction = new DisjunctiveChainedConnector();
		disjunction.setParentConnector(connector);
		disjunction.setChildCount(mappings.size());

		for (ArgumentMapping mapping : mappings) {
			if (mapping instanceof ArgumentMappingConjunction) {
				buildFilterObjects(annotation, (ArgumentMappingConjunction) mapping, disjunction);
			} else if (mapping instanceof ArgumentMappingJunction) {
				buildFilterObjects(annotation, (ArgumentMappingJunction) mapping, disjunction);
			} else {
				buildFilterObjects(annotation, mapping, connector);
			}
		}

		buildContext.register(filter, disjunction);
	}
}