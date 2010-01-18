package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;

import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.extcos.spi.ClassGenerator;
import net.sf.extcos.spi.ClassLoaderHolder;
import net.sf.extcos.util.Assert;

public class JavaClassGenerator implements ClassGenerator {
    private Log logger = LogFactory.getLog(getClass());
    private URL resourceUrl;
    
    public Class<?> generateClass() {
        URLClassLoader loader = new URLClassLoader(new URL[]{ resourceUrl }, ClassLoaderHolder.getClassLoader());
        try {
            return loader.loadClass(resourceUrl.getFile());
        } catch (ClassNotFoundException e) {
            //should never happen
            logger.debug("failed to generate class: ", e);
            return null;
        }
    }


    public void setResourceUrl(URL resourceUrl) {
        Assert.notNull(resourceUrl, iae());
        this.resourceUrl = resourceUrl;
    }
}
