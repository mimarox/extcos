package net.sf.extcos.spi;

public class QueryContext {
	private static QueryContext instance;
	
	private boolean includeEnums;
    private ClassLoader classLoader;
	
	private QueryContext() {}
	
	public static QueryContext getInstance() {
		if (instance == null) {
			instance = new QueryContext();
		}
		
		return instance;
	}

	public boolean isIncludeEnums() {
		return includeEnums;
	}

	public void setIncludeEnums(boolean includeEnums) {
		this.includeEnums = includeEnums;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	public void reset() {
		includeEnums = false;
		classLoader = null;
	}
}
