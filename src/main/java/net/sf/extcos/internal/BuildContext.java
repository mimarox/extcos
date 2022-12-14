package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.MergableConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

public class BuildContext {
	private static BuildContext instance;

	private final Map<TypeFilter, MergableConnector> annotatedConnectors = new HashMap<TypeFilter, MergableConnector>();
	private final Map<TypeFilter, MergableConnector> extendingConnectors = new HashMap<TypeFilter, MergableConnector>();
	private final Map<TypeFilter, MergableConnector> implementingConnectors = new HashMap<TypeFilter, MergableConnector>();
	private final Map<TypeFilter, MergableConnector> conjunctionConnectors = new HashMap<TypeFilter, MergableConnector>();
	private final Map<TypeFilter, MergableConnector> disjunctionConnectors = new HashMap<TypeFilter, MergableConnector>();

	private final Set<FilterObjects> annotatedWithFilterObjects = new ArraySet<FilterObjects>();
	private final Set<FilterObjects> annotationArgumentFilterObjects = new ArraySet<FilterObjects>();
	private final Set<FilterObjects> extendingFilterObjects = new ArraySet<FilterObjects>();
	private final Set<FilterObjects> implementingFilterObjects = new ArraySet<FilterObjects>();
	private final Set<ImmediateConnector> immediateConnectorRegistry = new ArraySet<ImmediateConnector>();

	private BuildContext() {
	}

	public static BuildContext getInstance() {
		if (instance == null) {
			instance = new BuildContext();
		}

		return instance;
	}

	public void addImmediateConnector(final ImmediateConnector connector) {
		Assert.notNull(connector, iae());
		immediateConnectorRegistry.add(connector);
	}

	public Set<FilterObjects> getAllFilterObjects() {
		Set<FilterObjects> all = new LinkedHashSet<FilterObjects>();

		all.addAll(annotatedWithFilterObjects);
		all.addAll(extendingFilterObjects);
		all.addAll(implementingFilterObjects);

		return all;
	}

	public MergableConnector getConnector(final TypeFilter typeFilter) {
		Assert.notNull(typeFilter, iae());

		if (typeFilter instanceof AnnotatedWithTypeFilter) {
			return annotatedConnectors.get(typeFilter);
		} else if (typeFilter instanceof ImplementingTypeFilter) {
			return implementingConnectors.get(typeFilter);
		} else if (typeFilter instanceof ExtendingTypeFilter) {
			return extendingConnectors.get(typeFilter);
		} else if (typeFilter instanceof TypeFilterConjunction) {
			return conjunctionConnectors.get(typeFilter);
		} else if (typeFilter instanceof TypeFilterDisjunction) {
			return disjunctionConnectors.get(typeFilter);
		} else {
			return null;
		}
	}

	public Set<FilterObjects> getExtendingFilterObjects() {
		return extendingFilterObjects;
	}

	public FilterObjects getFilterObjects(final ResourceMatcher resourceMatcher) {
		Assert.notNull(resourceMatcher, iae());

		if (resourceMatcher instanceof AnnotatedWithResourceMatcher) {
			return getFilterObjects(annotatedWithFilterObjects, resourceMatcher);
		} else if (resourceMatcher instanceof AnnotationArgumentResourceMatcher) {
			return getFilterObjects(annotationArgumentFilterObjects, resourceMatcher);
		} else if (resourceMatcher instanceof ExtendingResourceMatcher) {
			return getFilterObjects(extendingFilterObjects, resourceMatcher);
		} else if (resourceMatcher instanceof ImplementingResourceMatcher) {
			return getFilterObjects(implementingFilterObjects, resourceMatcher);
		} else {
			return null;
		}
	}

	private FilterObjects getFilterObjects(final Set<FilterObjects> filterObjectss,
			final ResourceMatcher resourceMatcher) {
		for (FilterObjects filterObjects : filterObjectss) {
			if (filterObjects.getResourceMatcher().equals(resourceMatcher)) {
				return filterObjects;
			}
		}

		return null;
	}

	public Iterable<ImmediateConnector> getImmediateConnectors() {
		return immediateConnectorRegistry;
	}

	public Set<FilterObjects> getImplementingFilterObjects() {
		return implementingFilterObjects;
	}

	public boolean isRegistered(final TypeFilter typeFilter) {
		Assert.notNull(typeFilter, iae());

		if (typeFilter instanceof AnnotatedWithTypeFilter) {
			return annotatedConnectors.containsKey(typeFilter);
		} else if (typeFilter instanceof ImplementingTypeFilter) {
			return implementingConnectors.containsKey(typeFilter);
		} else if (typeFilter instanceof ExtendingTypeFilter) {
			return extendingConnectors.containsKey(typeFilter);
		} else if (typeFilter instanceof TypeFilterConjunction) {
			return conjunctionConnectors.containsKey(typeFilter);
		} else if (typeFilter instanceof TypeFilterDisjunction) {
			return disjunctionConnectors.containsKey(typeFilter);
		} else {
			return false;
		}
	}

	public boolean isRegistered(final ResourceMatcher resourceMatcher) {
		Assert.notNull(resourceMatcher, iae());

		if (resourceMatcher instanceof AnnotatedWithResourceMatcher) {
			return getFilterObjects(annotatedWithFilterObjects, resourceMatcher) != null;
		} else if (resourceMatcher instanceof AnnotationArgumentResourceMatcher) {
			return getFilterObjects(annotationArgumentFilterObjects, resourceMatcher) != null;
		} else if (resourceMatcher instanceof ExtendingResourceMatcher) {
			return getFilterObjects(extendingFilterObjects, resourceMatcher) != null;
		} else if (resourceMatcher instanceof ImplementingResourceMatcher) {
			return getFilterObjects(implementingFilterObjects, resourceMatcher) != null;
		} else {
			return false;
		}
	}

	public void register(final TypeFilter typeFilter, final MergableConnector connector) {
		Assert.notNull(typeFilter, iae());
		Assert.notNull(connector, iae());

		if (typeFilter instanceof AnnotatedWithTypeFilter) {
			annotatedConnectors.put(typeFilter, connector);
		} else if (typeFilter instanceof ImplementingTypeFilter) {
			implementingConnectors.put(typeFilter, connector);
		} else if (typeFilter instanceof ExtendingTypeFilter) {
			extendingConnectors.put(typeFilter, connector);
		} else if (typeFilter instanceof TypeFilterConjunction) {
			conjunctionConnectors.put(typeFilter, connector);
		} else if (typeFilter instanceof TypeFilterDisjunction) {
			disjunctionConnectors.put(typeFilter, connector);
		}
	}

	public void register(final FilterObjects filterObjects) {
		Assert.notNull(filterObjects, iae());

		ResourceMatcher resourceMatcher = filterObjects.getResourceMatcher();

		if (resourceMatcher instanceof AnnotatedWithResourceMatcher) {
			annotatedWithFilterObjects.add(filterObjects);
		} else if (resourceMatcher instanceof AnnotationArgumentResourceMatcher) {
			annotationArgumentFilterObjects.add(filterObjects);
		} else if (resourceMatcher instanceof ExtendingResourceMatcher) {
			extendingFilterObjects.add(filterObjects);
		} else if (resourceMatcher instanceof ImplementingResourceMatcher) {
			implementingFilterObjects.add(filterObjects);
		}
	}

	public void reset() {
		annotatedConnectors.clear();
		annotatedWithFilterObjects.clear();
		annotationArgumentFilterObjects.clear();
		conjunctionConnectors.clear();
		disjunctionConnectors.clear();
		extendingConnectors.clear();
		extendingFilterObjects.clear();
		immediateConnectorRegistry.clear();
		implementingConnectors.clear();
		implementingFilterObjects.clear();
	}
}
