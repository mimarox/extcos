package net.sf.extcos;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.sf.extcos.collection.RandomPollingSet;
import net.sf.extcos.filter.BlacklistManager;
import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.FilterInterceptor;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.filter.MatchingChainedFilter;
import net.sf.extcos.filter.MergableConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.filter.builder.BuildContext;
import net.sf.extcos.filter.builder.BuildSupport;
import net.sf.extcos.filter.builder.FilterChainBuilder;
import net.sf.extcos.filter.builder.FilterObjectsBuilder;
import net.sf.extcos.filter.builder.FilterObjectsBuilderFactory;
import net.sf.extcos.internal.*;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.resource.ResourceResolver;
import net.sf.extcos.selector.*;
import net.sf.extcos.selector.Package;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

public class BindingDefinitions extends AbstractModule {
	protected void configure() {
		bindSelectorClasses();
		bindResourceClasses();
		bindFilterClasses();
		bindFilterBuilderClasses();
		bindCollections();
		bindMaps();
	}
	
	private void bindCollections() {
	    bind(new TypeLiteral<Set<Package>>(){}).
	    annotatedWith(name("bpsi.basePackages")).
	    to(new TypeLiteral<ArraySet<Package>>(){});
	    
	    bind(new TypeLiteral<Set<Class<?>>>(){}).
	    annotatedWith(name("cspi.classes")).
	    to(new TypeLiteral<ArraySet<Class<?>>>(){});
	    
	    bind(new TypeLiteral<Set<Resource>>(){}).
	    annotatedWith(name("cspi.filtered")).
	    to(new TypeLiteral<ArraySet<Resource>>(){});
	    
	    bind(new TypeLiteral<Set<Resource>>(){}).
	    annotatedWith(name("cspi.resources")).
	    to(new TypeLiteral<ArraySet<Resource>>(){});
	    
	    bind(new TypeLiteral<Set<StoreBinding>>(){}).
	    annotatedWith(name("fbi.storeBindings")).
	    to(new TypeLiteral<ArraySet<StoreBinding>>(){});
	    
        bind(new TypeLiteral<Set<FilterObjects>>(){}).
        annotatedWith(name("bci.annotatedWithFilterObjects")).
        to(new TypeLiteral<ArraySet<FilterObjects>>(){});
        
        bind(new TypeLiteral<Set<FilterObjects>>(){}).
        annotatedWith(name("bci.annotationArgumentFilterObjects")).
        to(new TypeLiteral<ArraySet<FilterObjects>>(){});
        
        bind(new TypeLiteral<Set<FilterObjects>>(){}).
        annotatedWith(name("bci.extendingFilterObjects")).
        to(new TypeLiteral<ArraySet<FilterObjects>>(){});
        
        bind(new TypeLiteral<Set<FilterObjects>>(){}).
        annotatedWith(name("bci.implementingFilterObjects")).
        to(new TypeLiteral<ArraySet<FilterObjects>>(){});
        
        bind(new TypeLiteral<Set<ImmediateConnector>>(){}).
        annotatedWith(name("bci.immediateConnectorRegistry")).
        to(new TypeLiteral<ArraySet<ImmediateConnector>>(){});
        
        bind(new TypeLiteral<Set<FilterObjects>>(){}).
        annotatedWith(name("foi.children")).
        to(new TypeLiteral<ArraySet<FilterObjects>>(){});
        
        bind(new TypeLiteral<Set<Filter>>(){}).
        annotatedWith(name("foi.filters")).
        to(new TypeLiteral<ArraySet<Filter>>(){});
        
        bind(new TypeLiteral<RandomPollingSet<Resource>>(){}).
        annotatedWith(name("mcfi.waitingResources")).
        to(new TypeLiteral<RandomPollingArraySet<Resource>>(){});
        
        bind(new TypeLiteral<Set<Connector>>(){}).
        annotatedWith(name("amc.connectors")).
        to(new TypeLiteral<ArraySet<Connector>>(){});
        
        bind(new TypeLiteral<Set<Resource>>(){}).
        annotatedWith(name("dcc.receivedResources")).
        to(new TypeLiteral<ArraySet<Resource>>(){});
	}
	
	private void bindMaps() {
	    bind(new TypeLiteral<Map<TypeFilter, MergableConnector>>(){}).
	    annotatedWith(name("bci.annotatedConnectors")).
	    to(new TypeLiteral<HashMap<TypeFilter, MergableConnector>>(){});
	    
        bind(new TypeLiteral<Map<TypeFilter, MergableConnector>>(){}).
        annotatedWith(name("bci.conjunctionConnectors")).
        to(new TypeLiteral<HashMap<TypeFilter, MergableConnector>>(){});
        
        bind(new TypeLiteral<Map<TypeFilter, MergableConnector>>(){}).
        annotatedWith(name("bci.disjunctionConnectors")).
        to(new TypeLiteral<HashMap<TypeFilter, MergableConnector>>(){});
        
        bind(new TypeLiteral<Map<TypeFilter, MergableConnector>>(){}).
        annotatedWith(name("bci.extendingConnectors")).
        to(new TypeLiteral<HashMap<TypeFilter, MergableConnector>>(){});
        
        bind(new TypeLiteral<Map<TypeFilter, MergableConnector>>(){}).
        annotatedWith(name("bci.implementingConnectors")).
        to(new TypeLiteral<HashMap<TypeFilter, MergableConnector>>(){});
        
        bind(new TypeLiteral<Map<Resource, Integer>>(){}).
        annotatedWith(name("ccc.waitingResources")).
        to(new TypeLiteral<HashMap<Resource, Integer>>(){});
	}
	
	private void bindSelectorClasses() {
        bind(ComponentSelectionProcessor.class).to(ComponentSelectionProcessorImpl.class);
        bind(ResourceTypeSelector.class).to(ResourceTypeSelectorImpl.class);
        bind(BasePackageSelector.class).to(BasePackageSelectorImpl.class);
        bind(ForwardingBuilder.class).to(ForwardingBuilderImpl.class);
        bind(ReturningSelector.class).to(ReturningSelectorImpl.class);
        bind(DirectReturning.class).toInstance(new EnumBasedReturning(Returning.ALL));
        bind(StoreReturning.class).toInstance(new EnumBasedReturning(Returning.NONE));
	}
	
	private void bindResourceClasses() {
        bind(ResourceResolver.class).to(ResourceResolverImpl.class);
	}
	
	private void bindFilterBuilderClasses() {
        bind(FilterChainBuilder.class).to(FilterChainBuilderImpl.class);
        bind(BuildContext.class).to(BuildContextImpl.class);
        bind(BuildSupport.class).to(BuildSupportImpl.class);
        bind(FilterObjectsBuilderFactory.class).to(FilterObjectsBuilderFactoryImpl.class);
        bind(FilterObjectsBuilder.class).annotatedWith(name("fobfi.annotatedWithBuilder")).to(AnnotatedWithFilterObjectsBuilder.class);
        bind(FilterObjectsBuilder.class).annotatedWith(name("fobfi.conjunctionBuilder")).to(FilterObjectsConjunctionBuilder.class);
        bind(FilterObjectsBuilder.class).annotatedWith(name("fobfi.disjunctionBuilder")).to(FilterObjectsDisjunctionBuilder.class);
        bind(FilterObjectsBuilder.class).annotatedWith(name("fobfi.extendingBuilder")).to(ExtendingFilterObjectsBuilder.class);
        bind(FilterObjectsBuilder.class).annotatedWith(name("fobfi.implementingBuilder")).to(ImplementingFilterObjectsBuilder.class);
	}
	
	private void bindFilterClasses() {
	    bind(ResultSetProvider.class).annotatedWith(name("fcbi.standardProvider")).to(StandardResultSetProvider.class);
	    bind(ResultSetProvider.class).annotatedWith(name("awfob.provider")).to(StandardResultSetProvider.class);
	    bind(ResultSetProvider.class).annotatedWith(name("efob.provider")).to(BlacklistAwareResultSetProvider.class);
	    bind(ResultSetProvider.class).annotatedWith(name("ifob.provider")).to(StandardResultSetProvider.class);
	    bind(FilterObjects.class).to(FilterObjectsImpl.class);
	    bind(MatchingChainedFilter.class).to(MatchingChainedFilterImpl.class);
	    bind(BlacklistManager.class).to(BlacklistManagerImpl.class);
	}
	
	@Provides
	@Named("bci.filterInterceptors")
	protected Set<Class<? extends FilterInterceptor>> filterInterceptors() {
		Set<Class<? extends FilterInterceptor>> interceptors =
			new LinkedHashSet<Class<? extends FilterInterceptor>>();
		
		interceptors.add(ThreadingFilterInterceptor.class);
		
		return interceptors;
	}
	
	private Named name(String name) {
	    return Names.named(name);
	}
}