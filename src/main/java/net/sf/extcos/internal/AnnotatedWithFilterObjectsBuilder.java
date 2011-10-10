package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;
import java.util.Set;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentMappingConjunction;
import net.sf.extcos.selector.annotation.ArgumentMappingDisjunction;
import net.sf.extcos.selector.annotation.ArgumentsDescriptor;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class AnnotatedWithFilterObjectsBuilder extends
AbstractFilterObjectsBuilder {

	@Inject
	@Named("awfob.provider")
	private ResultSetProvider provider;

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
		ConjunctiveChainedConnector conjunction = injector.getInstance(ConjunctiveChainedConnector.class);
		conjunction.setParentConnector(connector);
		conjunction.setChildCount(2);

		buildFilterObjects(annotation, conjunction);

		ArgumentMapping mapping = arguments.getArgumentMapping();
		if (mapping instanceof ArgumentMappingConjunction) {
			buildFilterObjects(annotation, (ArgumentMappingConjunction) mapping, conjunction);
		} else if (mapping instanceof ArgumentMappingDisjunction) {
			buildFilterObjects(annotation, (ArgumentMappingDisjunction) mapping, conjunction);
		} else {
			buildFilterObjects(annotation, mapping, connector);
		}
	}

	private void buildFilterObjects(final Class<? extends Annotation> annotation,
			final ArgumentMapping mapping, final Connector connector) {
		TypeFilter filter =
				new AnnotatedWithTypeFilterImpl(annotation, new ArgumentsDescriptorImpl(mapping));

		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
			return;
		}

		AnnotationArgumentResourceMatcher matcher = new AnnotationArgumentResourceMatcher(
				new AnnotationArgumentImpl(annotation, mapping));

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
				new ArgumentsDescriptorImpl(mappingConjunction));

		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
			return;
		}

		Set<ArgumentMapping> mappings = mappingConjunction.getMappings();

		ConjunctiveChainedConnector conjunction = injector.getInstance(ConjunctiveChainedConnector.class);
		conjunction.setParentConnector(connector);
		conjunction.setChildCount(mappings.size());

		for (ArgumentMapping mapping : mappings) {
			if (mapping instanceof ArgumentMappingConjunction) {
				buildFilterObjects(annotation, (ArgumentMappingConjunction) mapping, conjunction);
			} else if (mapping instanceof ArgumentMappingDisjunction) {
				buildFilterObjects(annotation, (ArgumentMappingDisjunction) mapping, conjunction);
			} else {
				buildFilterObjects(annotation, mapping, connector);
			}
		}

		buildContext.register(filter, conjunction);
	}

	private void buildFilterObjects(final Class<? extends Annotation> annotation,
			final ArgumentMappingDisjunction mappingDisjunction, final Connector connector) {
		TypeFilter filter =	new AnnotatedWithTypeFilterImpl(annotation,
				new ArgumentsDescriptorImpl(mappingDisjunction));

		if (buildContext.isRegistered(filter)) {
			buildContext.getConnector(filter).merge(connector);
			return;
		}

		Set<ArgumentMapping> mappings = mappingDisjunction.getMappings();

		DisjunctiveChainedConnector disjunction = injector.getInstance(DisjunctiveChainedConnector.class);
		disjunction.setParentConnector(connector);
		disjunction.setChildCount(mappings.size());

		for (ArgumentMapping mapping : mappings) {
			if (mapping instanceof ArgumentMappingConjunction) {
				buildFilterObjects(annotation, (ArgumentMappingConjunction) mapping, disjunction);
			} else if (mapping instanceof ArgumentMappingDisjunction) {
				buildFilterObjects(annotation, (ArgumentMappingDisjunction) mapping, disjunction);
			} else {
				buildFilterObjects(annotation, mapping, connector);
			}
		}

		buildContext.register(filter, disjunction);
	}
}