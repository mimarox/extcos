package net.sf.extcos.internal;

import static net.sf.extcos.util.Assert.iae;
import static net.sf.extcos.util.StringUtils.append;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.extcos.spi.ClassGenerator;
import net.sf.extcos.util.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JavaClassGenerator implements ClassGenerator {
    private static class ResourceClassLoader extends ClassLoader {
        private static Log logger = LogFactory.getLog(JavaClassGenerator.class);
    	
    	@SuppressWarnings("deprecation")
		public Class<?> loadClass(URL classUrl) {
    		try {
				byte[] classBytes = readBytes(new BufferedInputStream(classUrl.openStream()));
    			Class<?> clazz = defineClass(classBytes, 0, classBytes.length);
				resolveClass(clazz);
				return clazz;
			} catch (Exception e) {
				logger.error(append("Error creating class from URL [",
						classUrl.toString(), "]"), e);
				return null;
			}
    	}

		private byte[] readBytes(InputStream classStream) throws IOException {
			List<Byte> buffer = new ArrayList<Byte>();
			int readByte;
			
			while((readByte = classStream.read()) != -1) {
				buffer.add((byte)readByte);
			}
			
			byte[] bytes = new byte[buffer.size()];
			
			for (int i = 0; i < buffer.size(); i++) {
				bytes[i] = buffer.get(i);
			}
			
			return bytes;
		}
    }
	
	
    private static ResourceClassLoader loader;
    private URL resourceUrl;
    
    public Class<?> generateClass() {
        return getResourceClassLoader().loadClass(resourceUrl);
    }


    private static ResourceClassLoader getResourceClassLoader() {
		if (loader == null) {
			loader = new ResourceClassLoader();
		}
    	return loader;
	}


	public void setResourceUrl(URL resourceUrl) {
        Assert.notNull(resourceUrl, iae());
        this.resourceUrl = resourceUrl;
    }
}
