package net.sf.extcos;

import static net.sf.extcos.internal.JavaClassResourceType.javaClasses;
import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;

import net.sf.extcos.internal.*;
import net.sf.extcos.selector.*;
import net.sf.extcos.selector.annotation.ArgumentKey;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentMappingConjunction;
import net.sf.extcos.selector.annotation.ArgumentMappingDisjunction;
import net.sf.extcos.selector.annotation.ArgumentValue;
import net.sf.extcos.selector.annotation.ArgumentsDescriptor;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.Assert;

public abstract class ClassQuery implements ClassSelector {
	private ResourceTypeSelector resourceSelector;
	private boolean entryAllowed = true;
	private boolean selectAllowed = true;

	protected abstract void query();

	public final synchronized void configure(
			ResourceTypeSelector resourceTypeSelector) {
		Assert.state(entryAllowed, "Re-entry is not allowed.");
		Assert.notNull(resourceTypeSelector, iae(),
				"resourceSelector must not be null");

		this.resourceSelector = resourceTypeSelector;

		try {
			query();
		} finally {
			entryAllowed = false;
		}
	}

	protected ArgumentMappingConjunction and(ArgumentMapping... mappings) {
		return new ArgumentMappingConjunctionImpl(mappings);
	}

	protected ArgumentMappingDisjunction or(ArgumentMapping... mappings) {
		return new ArgumentMappingDisjunctionImpl(mappings);
	}

	protected ArgumentKey key(String key) {
		return new ArgumentKeyImpl(key);
	}

	protected ArgumentValue value(Object value) {
		return new ArgumentValueImpl(value);
	}

	protected ArgumentMapping mapping(ArgumentKey key, ArgumentValue value) {
		return new ArgumentMappingImpl(key, value);
	}

	protected ArgumentsDescriptor withArgument(ArgumentKey key,
			ArgumentValue value) {
		return new ArgumentsDescriptorImpl(key, value);
	}

	protected ArgumentsDescriptor withArguments(
			ArgumentMappingConjunction arguments) {
		return new ArgumentsDescriptorImpl(arguments);
	}

	protected ArgumentsDescriptor withArguments(
			ArgumentMappingDisjunction arguments) {
		return new ArgumentsDescriptorImpl(arguments);
	}

	protected TypeFilterJunction and(MultipleTypeFilter... filters) {
		return new TypeFilterConjunction(filters);
	}

	protected TypeFilterJunction and(ExtendingTypeFilter filter,
			MultipleTypeFilter... filters) {
		return new TypeFilterConjunction(filter, filters);
	}

	protected TypeFilterJunction or(TypeFilter... filters) {
		return new TypeFilterDisjunction(filters);
	}

	protected ExtendingTypeFilter subclassOf(Class<?> clazz) {
		return new ExtendingTypeFilterImpl(clazz);
	}

	protected ImplementingTypeFilter implementorOf(Class<?>... interfaces) {
		return new ImplementingTypeFilterImpl(interfaces);
	}

	protected AnnotatedWithTypeFilter annotatedWith(
			Class<? extends Annotation> annotation) {
		return new AnnotatedWithTypeFilterImpl(annotation, null);
	}

	protected AnnotatedWithTypeFilter annotatedWith(
			Class<? extends Annotation> annotation,
			ArgumentsDescriptor arguments) {
		return new AnnotatedWithTypeFilterImpl(annotation, arguments);
	}

	protected StoreReturning none() {
		return new EnumBasedReturning(Returning.NONE);
	}

	protected DirectReturning all() {
		return new EnumBasedReturning(Returning.ALL);
	}

	protected StoreReturning allMerged() {
		return new EnumBasedReturning(Returning.ALL_MERGED);
	}

	protected DirectReturning allExtending(Class<?> clazz) {
		return new TypeFilterBasedReturning(subclassOf(clazz));
	}

	protected DirectReturning allImplementing(Class<?>... interfaces) {
		return new TypeFilterBasedReturning(implementorOf(interfaces));
	}

	protected DirectReturning allAnnotatedWith(
			Class<? extends Annotation> annotation) {
		return new TypeFilterBasedReturning(annotatedWith(annotation));
	}

	protected DirectReturning allAnnotatedWith(
			Class<? extends Annotation> annotation,
			ArgumentsDescriptor arguments) {
		return new TypeFilterBasedReturning(annotatedWith(annotation,
				arguments));
	}

	protected DirectReturning allBeing(TypeFilterJunction filter) {
		return new TypeFilterBasedReturning(filter);
	}

	protected <T> TypedStoreBindingBuilder<T> thoseExtending(Class<T> clazz) {
		return new TypedStoreBindingBuilderImpl<T>(subclassOf(clazz));
	}

	protected <T> TypedStoreBindingBuilder<T> thoseImplementing(
			Class<T> interfaze) {
		return new TypedStoreBindingBuilderImpl<T>(implementorOf(interfaze));
	}

	protected TypelessStoreBindingBuilder thoseImplementing(
			Class<?>... interfaces) {
		return new TypelessStoreBindingBuilderImpl(implementorOf(interfaces));
	}

	protected TypelessStoreBindingBuilder thoseAnnotatedWith(
			Class<? extends Annotation> annotation) {
		return new TypelessStoreBindingBuilderImpl(annotatedWith(annotation));
	}

	protected TypelessStoreBindingBuilder thoseAnnotatedWith(
			Class<? extends Annotation> annotation,
			ArgumentsDescriptor arguments) {
		return new TypelessStoreBindingBuilderImpl(
				annotatedWith(annotation, arguments));
	}

	protected TypelessStoreBindingBuilder thoseBeing(
			TypeFilterJunction filter) {
		return new TypelessStoreBindingBuilderImpl(filter);
	}
	
	protected synchronized BasePackageSelector select() {
		return select(javaClasses());
	}

	protected synchronized BasePackageSelector select(
			ResourceType... resourceTypes) {
		Assert.state(selectAllowed,
				"calling select() more than once is not allowed");

		try {
			return resourceSelector.select(resourceTypes);
		} finally {
			selectAllowed = false;
		}
	}
}