package net.sf.extcos;

import static net.sf.extcos.internal.JavaClassResourceType.javaClasses;
import static net.sf.extcos.util.Assert.iae;

import java.lang.annotation.Annotation;

import net.sf.extcos.internal.AnnotatedWithTypeFilterImpl;
import net.sf.extcos.internal.ArgumentMappingImpl;
import net.sf.extcos.internal.EnumBasedReturning;
import net.sf.extcos.internal.ExtendingTypeFilterImpl;
import net.sf.extcos.internal.ImplementingTypeFilterImpl;
import net.sf.extcos.internal.Returning;
import net.sf.extcos.internal.TypeFilterBasedReturning;
import net.sf.extcos.internal.TypeFilterConjunction;
import net.sf.extcos.internal.TypeFilterDisjunction;
import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.BasePackageSelector;
import net.sf.extcos.selector.DirectReturning;
import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.selector.MultipleTypeFilter;
import net.sf.extcos.selector.PackagePatternBuilder;
import net.sf.extcos.selector.ResourceTypeSelector;
import net.sf.extcos.selector.StoreReturning;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypeFilterJunction;
import net.sf.extcos.selector.TypedStoreBindingBuilder;
import net.sf.extcos.selector.TypelessStoreBindingBuilder;
import net.sf.extcos.selector.annotation.ArgumentKey;
import net.sf.extcos.selector.annotation.ArgumentMapping;
import net.sf.extcos.selector.annotation.ArgumentMappingConjunction;
import net.sf.extcos.selector.annotation.ArgumentMappingDisjunction;
import net.sf.extcos.selector.annotation.ArgumentMappingJunction;
import net.sf.extcos.selector.annotation.ArgumentValue;
import net.sf.extcos.selector.annotation.ArgumentsDescriptor;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.ArrayUtils;
import net.sf.extcos.util.Assert;

public abstract class ComponentQuery {
	private ResourceTypeSelector resourceSelector;
	private boolean entryAllowed = true;
	private boolean selectAllowed = true;

	protected abstract void query();

	public final synchronized void configure(
			final ResourceTypeSelector resourceTypeSelector) {
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

	protected ArgumentMappingJunction and(final ArgumentMapping... mappings) {
		return new ArgumentMappingConjunction(mappings);
	}

	protected ArgumentMappingJunction or(final ArgumentMapping... mappings) {
		return new ArgumentMappingDisjunction(mappings);
	}

	protected ArgumentKey key(final String key) {
		return new ArgumentKey(key);
	}

	protected ArgumentValue value(final Object value) {
		return new ArgumentValue(value);
	}

	protected ArgumentMapping mapping(final ArgumentKey key, final ArgumentValue value) {
		return new ArgumentMappingImpl(key, value);
	}

	protected ArgumentsDescriptor withArgument(final ArgumentKey key,
			final ArgumentValue value) {
		return new ArgumentsDescriptor(key, value);
	}

	protected ArgumentsDescriptor withArguments(
			final ArgumentMappingJunction arguments) {
		return new ArgumentsDescriptor(arguments);
	}

	protected TypeFilterJunction and(final MultipleTypeFilter... filters) {
		return new TypeFilterConjunction(filters);
	}

	protected TypeFilterJunction and(final ExtendingTypeFilter filter,
			final MultipleTypeFilter... filters) {
		return new TypeFilterConjunction(filter, filters);
	}

	protected TypeFilterJunction or(final TypeFilter... filters) {
		return new TypeFilterDisjunction(filters);
	}

	protected ExtendingTypeFilter subclassOf(final Class<?> clazz) {
		return new ExtendingTypeFilterImpl(clazz);
	}

	protected ImplementingTypeFilter implementorOf(final Class<?>... interfaces) {
		return new ImplementingTypeFilterImpl(interfaces);
	}

	protected AnnotatedWithTypeFilter annotatedWith(
			final Class<? extends Annotation> annotation) {
		return new AnnotatedWithTypeFilterImpl(annotation, null);
	}

	protected AnnotatedWithTypeFilter annotatedWith(
			final Class<? extends Annotation> annotation,
			final ArgumentsDescriptor arguments) {
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

	protected DirectReturning allExtending(final Class<?> clazz) {
		return new TypeFilterBasedReturning(subclassOf(clazz));
	}

	protected DirectReturning allImplementing(final Class<?>... interfaces) {
		return new TypeFilterBasedReturning(implementorOf(interfaces));
	}

	protected DirectReturning allAnnotatedWith(
			final Class<? extends Annotation> annotation) {
		return new TypeFilterBasedReturning(annotatedWith(annotation));
	}

	protected DirectReturning allAnnotatedWith(
			final Class<? extends Annotation> annotation,
			final ArgumentsDescriptor arguments) {
		return new TypeFilterBasedReturning(annotatedWith(annotation,
				arguments));
	}

	protected DirectReturning allBeing(final TypeFilterJunction filter) {
		return new TypeFilterBasedReturning(filter);
	}

	protected <T> TypedStoreBindingBuilder<T> thoseExtending(final Class<T> clazz) {
		return new TypedStoreBindingBuilder<T>(subclassOf(clazz));
	}

	protected <T> TypedStoreBindingBuilder<T> thoseImplementing(
			final Class<T> interfaze) {
		return new TypedStoreBindingBuilder<T>(implementorOf(interfaze));
	}

	protected TypelessStoreBindingBuilder thoseImplementing(
			final Class<?>... interfaces) {
		return new TypelessStoreBindingBuilder(implementorOf(interfaces));
	}

	protected TypelessStoreBindingBuilder thoseAnnotatedWith(
			final Class<? extends Annotation> annotation) {
		return new TypelessStoreBindingBuilder(annotatedWith(annotation));
	}

	protected TypelessStoreBindingBuilder thoseAnnotatedWith(
			final Class<? extends Annotation> annotation,
			final ArgumentsDescriptor arguments) {
		return new TypelessStoreBindingBuilder(
				annotatedWith(annotation, arguments));
	}

	protected TypelessStoreBindingBuilder thoseBeing(
			final TypeFilterJunction filter) {
		return new TypelessStoreBindingBuilder(filter);
	}

	protected PackagePatternBuilder allSubPackages(String subPackagePattern) {
		return new PackagePatternBuilder(subPackagePattern);
	}

	protected String[] join(String[]... packagePatterns) {
		return ArrayUtils.join(packagePatterns);
	}

	protected synchronized BasePackageSelector select() {
		return select(javaClasses());
	}

	protected synchronized BasePackageSelector select(
			final ResourceType... resourceTypes) {
		Assert.state(selectAllowed,
				"calling select() more than once is not allowed");

		try {
			return resourceSelector.select(resourceTypes);
		} finally {
			selectAllowed = false;
		}
	}
}