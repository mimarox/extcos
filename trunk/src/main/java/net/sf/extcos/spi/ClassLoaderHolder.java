package net.sf.extcos.spi;

@Deprecated
public class ClassLoaderHolder {
    private static ClassLoader classLoader;
    
    private ClassLoaderHolder() {} // no instantiation possible
    
    public static ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public static void setClassLoader(ClassLoader classLoader) {
    	ClassLoaderHolder.classLoader = classLoader;
    }
}