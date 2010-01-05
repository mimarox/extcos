package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.util.Set;

import net.sf.extcos.filter.Connector;
import net.sf.extcos.filter.FilterObjects;
import net.sf.extcos.filter.MultiplexingConnector;
import net.sf.extcos.filter.ResultSetProvider;
import net.sf.extcos.selector.ImplementingTypeFilter;
import net.sf.extcos.selector.TypeFilter;
import net.sf.extcos.util.Assert;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ImplementingFilterObjectsBuilder extends AbstractFilterObjectsBuilder {

    @Inject
    @Named("ifob.provider")
    private ResultSetProvider provider;
    
    @Override
    public void buildFilterObjects(TypeFilter filter, Connector connector) {
        Assert.notNull(filter, iae());
        Assert.isTrue(filter instanceof ImplementingTypeFilter, iae());
        Assert.notNull(connector, iae());
        
        Set<Class<?>> interfaces = ((ImplementingTypeFilter) filter).getInterfaces();
        
        ConjunctiveChainedConnector conjunction = injector.getInstance(ConjunctiveChainedConnector.class);
        conjunction.setParentConnector(connector);
        conjunction.setChildCount(interfaces.size());
        
        for (Class<?> interfaze : interfaces) {
        	ImplementingResourceMatcher matcher = new ImplementingResourceMatcher(interfaze);
        	
        	if (buildContext.isRegistered(matcher)) {
        		FilterObjects fo = buildContext.getFilterObjects(matcher);
        		fo.getResourceDispatcher().addConnector(conjunction);
        	} else {
    			MultiplexingConnector dispatcher =
    				new StandardMultiplexingConnector();
    			
    			dispatcher.addConnector(conjunction);
    			
    			FilterObjects filterObjects =
    				createFilterObjects(dispatcher, matcher, provider);
    			
    			buildContext.register(filterObjects);
        	}
        }
        
		buildContext.register(filter, conjunction);
    }
}