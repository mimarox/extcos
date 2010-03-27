package net.sf.extcos;

import java.util.Set;

import net.sf.extcos.selector.ClassSelectionProcessor;
import net.sf.extcos.selector.ClassSelector;
import net.sf.extcos.spi.ClassLoaderHolder;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class ComponentScanner {
	public Set<Class<?>> getClasses(ComponentQuery componentQuery) {
		return getClasses(componentQuery, getDefaultClassLoader());
	}

	public Set<Class<?>> getClasses(final ComponentQuery componentQuery,
			final ClassLoader classLoader) {
		Injector injector = Guice.createInjector(new BindingDefinitions(),
				new AbstractModule() {
					protected void configure() {
						bind(ClassSelector.class).toInstance(componentQuery);
						bind(ClassLoader.class).toInstance(classLoader);
					}
				});

		ClassLoaderHolder.setClassLoader(classLoader);
		
		ClassSelectionProcessor processor = injector
				.getInstance(ClassSelectionProcessor.class);

		return processor.process();
	}
	
	private ClassLoader getDefaultClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}
}