package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.StringUtils.append;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sf.extcos.filter.ChainedFilter;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.FilterInterceptor;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.MergableConnector;
import net.sf.extcos.filter.ResourceMatcher;
import net.sf.extcos.filter.builder.BuildContext;
import net.sf.extcos.selector.AnnotatedWithTypeFilter;
import net.sf.extcos.selector.ExtendingTypeFilter;
import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.selector.TypeFilterConjunction;
import net.sf.extcos.selector.TypeFilterDisjunction;
import net.sf.extcos.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class BuildContextImpl implements BuildContext {
	private static Log logger = LogFactory.getLog(BuildContextImpl.class);

	@Inject
	@Named("bci.annotatedConnectors")
	private Map<TypeFilter, MergableConnector> annotatedConnectors;
	
	@Inject
	@Named("bci.extendingConnectors")
	private Map<TypeFilter, MergableConnector> extendingConnectors;
	
	@Inject
	@Named("bci.implementingConnectors")
	private Map<TypeFilter, MergableConnector> implementingConnectors;
	
	@Inject
	@Named("bci.conjunctionConnectors")
	private Map<TypeFilter, MergableConnector> conjunctionConnectors;
	
	@Inject
	@Named("bci.disjunctionConnectors")
	private Map<TypeFilter, MergableConnector> disjunctionConnectors;
	
	@Inject
	@Named("bci.annotatedWithFilterObjects")
	private Set<FilterObjects> annotatedWithFilterObjects;

	@Inject
	@Named("bci.annotationArgumentFilterObjects")
	private Set<FilterObjects> annotationArgumentFilterObjects;
	
	@Inject
	@Named("bci.extendingFilterObjects")
	private Set<FilterObjects> extendingFilterObjects;

	@Inject
	@Named("bci.implementingFilterObjects")
	private Set<FilterObjects> implementingFilterObjects;

	@Inject
	@Named("bci.immediateConnectorRegistry")
	private Set<ImmediateConnector> immediateConnectorRegistry;

	@Inject
	@Named("bci.filterInterceptors")
	private Set<Class<? extends FilterInterceptor>> filterInterceptors;

	@Inject
	private Injector injector;
	
	public void addImmediateConnector(ImmediateConnector connector) {
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

	public MergableConnector getConnector(TypeFilter typeFilter) {
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

	public FilterObjects getFilterObjects(ResourceMatcher resourceMatcher) {
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

	private FilterObjects getFilterObjects(Set<FilterObjects> filterObjectss,
			ResourceMatcher resourceMatcher) {
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

	public boolean isRegistered(TypeFilter typeFilter) {
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

	public boolean isRegistered(ResourceMatcher resourceMatcher) {
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

	public void register(TypeFilter typeFilter, MergableConnector connector) {
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

	public void register(FilterObjects filterObjects) {
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

	public Filter prependInterceptors(ChainedFilter filter) {
		if (this.filterInterceptors.isEmpty()) {
			return filter;
		}

		Set<FilterInterceptor> filterInterceptors = new LinkedHashSet<FilterInterceptor>();

		for (Class<? extends FilterInterceptor> clazz : this.filterInterceptors) {
			try {
				filterInterceptors.add(injector.getInstance(clazz));
			} catch (Exception e) {
				logger.debug(append("Creating a new filter interceptor of ",
						clazz, " caused an exception"), e);
			}
		}

		if (filterInterceptors.isEmpty()) {
			return filter;
		}

		FilterInterceptor firstInterceptor = null;
		FilterInterceptor currentInterceptor = null;

		for (FilterInterceptor nextInterceptor : filterInterceptors) {
			if (firstInterceptor == null) {
				firstInterceptor = nextInterceptor;
				currentInterceptor = firstInterceptor;
			} else {
				currentInterceptor.setInterceptedFilter(nextInterceptor);
				currentInterceptor = nextInterceptor;
			}
		}

		currentInterceptor.setInterceptedFilter(filter);
		return firstInterceptor;
	}
}