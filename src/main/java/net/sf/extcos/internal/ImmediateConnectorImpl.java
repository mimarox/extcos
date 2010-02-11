package net.sf.extcos.internal;

import static net.sf.extcos.util.StringUtils.append;

import java.util.Set;

import net.sf.extcos.classgeneration.ClassGenerationListener;
import net.sf.extcos.filter.ImmediateConnector;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ImmediateConnectorImpl implements ImmediateConnector {
	
	private static Log logger = LogFactory.getLog(ImmediateConnectorImpl.class);
	
	private Set<Class<?>> receivingSet;
	private Set<Resource> filtered;
	
	public void setReceivingSet(Set<Class<?>> receivingSet) {
		try {
			Assert.notNull(receivingSet, IllegalArgumentException.class,
					"receivingSet must not be null");
			
			this.receivingSet = receivingSet;
			
			if (logger.isTraceEnabled()) {
				logger.trace("successfully set receivingSet");
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set receivingSet", e);
		}
	}

	public void setFilteredRegistry(Set<Resource> filtered) {
		try {
			Assert.notNull(filtered, IllegalArgumentException.class,
					"filtered must not be null");
			
			this.filtered = filtered;
			
			if (logger.isTraceEnabled()) {
				logger.trace("successfully set filtered registry");
			}
		} catch(IllegalArgumentException e) {
			logger.debug("couldn't set filtered registry", e);
		}
	}

	public void connect(Resource resource) {
		if (receivingSet == null) {
			logger.debug("can't connect: receivingSet is not set");
			return;
		}
		
		resource.addClassGenerationListener(new ClassGenerationListener(){
			private Log logger = LogFactory.getLog("ClassGenerationListener");
			
			public <T> void classGenerated(Class<? extends T> clazz) {
				if (clazz == null) return;
				
				receivingSet.add(clazz);
				
				if (logger.isTraceEnabled()) {
					logger.trace(append("successfully added generated ",
							clazz));
				}
			}
		});
		
		filtered.add(resource);
		
		if (logger.isTraceEnabled()) {
			logger.trace(append("successfully connected resource ", resource));
		}
	}

	public Set<Class<?>> getReceivingSet() {
		return receivingSet;
	}
}