package net.sf.extcos.spi;

public class ClassLoaderHolder {
    private static ClassLoader classLoader;
    
    private ClassLoaderHolder() {} // no instantiation possible
    
    public static ClassLoader getClassLoader() {
        return classLoader;
    }
    
    public static void setClassLoader(ClassLoader classLoader) {
        if (ClassLoaderHolder.classLoader == null) {
            ClassLoaderHolder.classLoader = classLoader;
        } else {
            throw new IllegalStateException("class loader already set");
        }
    }
}