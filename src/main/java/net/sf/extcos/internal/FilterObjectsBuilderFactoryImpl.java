package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import net.sf.extcos.filter.builder.FilterObjectsBuilder;
import net.sf.extcos.filter.builder.FilterObjectsBuilderFactory;
import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class FilterObjectsBuilderFactoryImpl implements
		FilterObjectsBuilderFactory {

	@Inject
	@Named("fobfi.annotatedWithBuilder")
	private FilterObjectsBuilder annotatedWithBuilder;
	
	@Inject
	@Named("fobfi.extendingBuilder")
	private FilterObjectsBuilder extendingBuilder;
	
	@Inject
	@Named("fobfi.implementingBuilder")
	private FilterObjectsBuilder implementingBuilder;
	
	@Inject
	@Named("fobfi.conjunctionBuilder")
	private FilterObjectsBuilder conjunctionBuilder;
	
	@Inject
	@Named("fobfi.disjunctionBuilder")
	private FilterObjectsBuilder disjunctionBuilder;
	
	public FilterObjectsBuilder getFilterObjectsBuilder(TypeFilter typeFilter) {
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