package systemtests;

import static org.testng.Assert.assertTrue;

import java.io.Closeable;
import java.util.Set;

import javax.swing.JPanel;

import net.sf.extcos.filter.Filter;
import net.sf.extcos.internal.AnnotatedWithTypeFilterImpl;
import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.internal.EnumBasedReturning;
import net.sf.extcos.internal.ExtendingTypeFilterImpl;
import net.sf.extcos.internal.FilterChainBuilder;
import net.sf.extcos.internal.ImplementingTypeFilterImpl;
import net.sf.extcos.internal.Returning;
import net.sf.extcos.internal.TypeFilterConjunction;
import net.sf.extcos.internal.TypeFilterDisjunction;
import net.sf.extcos.resource.Resource;
import net.sf.extcos.selector.StoreBinding;
import net.sf.extcos.selector.StoreReturning;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FilterChainBuilderImplTest {
	private FilterChainBuilder builder;

	@BeforeClass
	public void initBuilder() {
		builder = new FilterChainBuilder();
	}

	@Test
	public void testBuild() {
		Set<StoreBinding> storeBindings = new ArraySet<StoreBinding>();

		// classes extending JPanel and implementing Closeable
		storeBindings.add(new StoreBinding(new TypeFilterConjunction(
				new ExtendingTypeFilterImpl(JPanel.class),
				new ImplementingTypeFilterImpl(Closeable.class)),
				new ArraySet<Class<?>>()));

		// classes implementing Cloneable or annotated with @Test
		storeBindings.add(new StoreBinding(new TypeFilterDisjunction(
				new ImplementingTypeFilterImpl(Cloneable.class),
				new AnnotatedWithTypeFilterImpl(Test.class, null)),
				new ArraySet<Class<?>>()));

		StoreReturning returning = new EnumBasedReturning(Returning.ALL_MERGED);

		Filter filter = builder.build(storeBindings, returning,
				new ArraySet<Resource>(), new ArraySet<Class<?>>());

		assertTrue(filter != null);
	}
}
