package systemtests;

import static org.testng.Assert.assertTrue;

import java.io.Closeable;
import java.util.Set;

import javax.swing.JPanel;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.BindingDefinitions;
import net.sf.extcos.filter.Filter;
import net.sf.extcos.filter.builder.FilterChainBuilder;
import net.sf.extcos.internal.AnnotatedWithTypeFilterImpl;
import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.internal.EnumBasedReturning;
import net.sf.extcos.internal.ExtendingTypeFilterImpl;
import net.sf.extcos.internal.ImplementingTypeFilterImpl;
import net.sf.extcos.internal.Returning;
import net.sf.extcos.internal.StoreBindingImpl;
import net.sf.extcos.internal.TypeFilterConjunction;
import net.sf.extcos.internal.TypeFilterDisjunction;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.selector.ComponentSelector;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class FilterChainBuilderImplTest {
	private FilterChainBuilder builder;

	@BeforeClass
	public void initBuilder() {
		Injector injector = Guice.createInjector(new BindingDefinitions(),
				new AbstractModule() {
					protected void configure() {
						bind(ComponentSelector.class).toInstance(
							new ComponentQuery() {
								@Override protected void query() {}
							}
						);
						bind(ClassLoader.class).toInstance(
								Thread.currentThread().getContextClassLoader());
					}
				});

		builder = injector.getInstance(FilterChainBuilder.class);
	}

	@Test
	public void testBuild() {
		Set<StoreBinding> storeBindings = new ArraySet<StoreBinding>();

		// classes extending JPanel and implementing Closeable
		storeBindings.add(new StoreBindingImpl(new TypeFilterConjunction(
				new ExtendingTypeFilterImpl(JPanel.class),
				new ImplementingTypeFilterImpl(Closeable.class)),
				new ArraySet<Class<?>>()));

		// classes implementing Cloneable or annotated with @Test
		storeBindings.add(new StoreBindingImpl(new TypeFilterDisjunction(
				new ImplementingTypeFilterImpl(Cloneable.class),
				new AnnotatedWithTypeFilterImpl(Test.class, null)),
				new ArraySet<Class<?>>()));

		StoreReturning returning = new EnumBasedReturning(Returning.ALL_MERGED);

		Filter filter = builder.build(storeBindings, returning,
				new ArraySet<Resource>(), new ArraySet<Class<?>>());

		assertTrue(filter != null);
	}
}
