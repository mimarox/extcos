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
    private class ResourceClassLoader extends ClassLoader {
    	
    	@SuppressWarnings("deprecation")
		public Class<?> loadClass() {
    		try {
				byte[] classBytes = readBytes(new BufferedInputStream(resourceUrl.openStream()));
    			Class<?> clazz = defineClass(classBytes, 0, classBytes.length);
				resolveClass(clazz);
				return clazz;
			} catch (Exception e) {
				logger.error(append("Error creating class from URL [",
						resourceUrl.toString(), "]"), e);
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
	
	private Log logger = LogFactory.getLog(getClass());
    private URL resourceUrl;
    
    public Class<?> generateClass() {
        return new ResourceClassLoader().loadClass();
    }


    public void setResourceUrl(URL resourceUrl) {
        Assert.notNull(resourceUrl, iae());
        this.resourceUrl = resourceUrl;
    }
}
