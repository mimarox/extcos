package net.sf.extcos.internal.factory;

import static net.sf.extcos.util.Assert.iae;
import net.sf.extcos.filter.builder.FilterObjectsBuilder;
import net.sf.extcos.internal.AnnotatedWithFilterObjectsBuilder;
import net.sf.extcos.internal.ExtendingFilterObjectsBuilder;
import net.sf.extcos.internal.FilterObjectsConjunctionBuilder;
import net.sf.extcos.internal.FilterObjectsDisjunctionBuilder;
import net.sf.extcos.internal.ImplementingFilterObjectsBuilder;
import net.sf.extcos.internal.TypeFilterConjunction;
import net.sf.extcos.internal.TypeFilterDisjunction;
import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class FilterObjectsBuilderFactory {
	private static FilterObjectsBuilderFactory instance;

	private final FilterObjectsBuilder annotatedWithBuilder = new AnnotatedWithFilterObjectsBuilder();
	private final FilterObjectsBuilder extendingBuilder = new ExtendingFilterObjectsBuilder();
	private final FilterObjectsBuilder implementingBuilder = new ImplementingFilterObjectsBuilder();
	private final FilterObjectsBuilder conjunctionBuilder = new FilterObjectsConjunctionBuilder();
	private final FilterObjectsBuilder disjunctionBuilder = new FilterObjectsDisjunctionBuilder();

	private FilterObjectsBuilderFactory() {
	}

	public static FilterObjectsBuilderFactory getInstance() {
		if (instance == null) {
			instance = new FilterObjectsBuilderFactory();
		}

		return instance;
	}

	public FilterObjectsBuilder getFilterObjectsBuilder(final TypeFilter typeFilter) {
		Assert.notNull(typeFilter, iae());

		if (typeFilter instanceof AnnotatedWithTypeFilter) {
			return annotatedWithBuilder;
		} else if (typeFilter instanceof ImplementingTypeFilter) {
			return implementingBuilder;
		} else if (typeFilter instanceof ExtendingTypeFilter) {
			return extendingBuilder;
		} else if (typeFilter instanceof TypeFilterConjunction) {
			return conjunctionBuilder;
		} else if (typeFilter instanceof TypeFilterDisjunction) {
			return disjunctionBuilder;
		} else {
			return null;
		}
	}
}