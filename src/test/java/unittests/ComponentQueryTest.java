package unittests;

import static net.sf.extcos.internal.JavaClassResourceType.javaClasses;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.internal.BasePackageSelectorImpl;
import net.sf.extcos.internal.ForwardingBuilderImpl;
import net.sf.extcos.internal.ResourceTypeSelectorImpl;
import net.sf.extcos.selector.BasePackageSelector;
import net.sf.extcos.selector.ComponentSelector;
import net.sf.extcos.selector.ResourceTypeSelector;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.PropertyInjector;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ComponentQueryTest {

	ResourceTypeSelector resourceTypeSelector;
	
	@BeforeMethod
	public void init() throws Exception {
		resourceTypeSelector = new ResourceTypeSelectorImpl();
		BasePackageSelector basePackageSelector = new BasePackageSelectorImpl();
		
		PropertyInjector injector = new PropertyInjector();
		injector.setTarget(resourceTypeSelector);
		injector.inject("basePackageSelector", basePackageSelector);
		
		injector.setTarget(basePackageSelector);
		injector.inject("forwardingBuilder", new ForwardingBuilderImpl());
		injector.inject("basePackages", new ArraySet<Package>());
	}
	
	@Test
	public void testEmptySelect() {
		ComponentSelector selector = new ComponentQuery() {
			protected void query() {
				select();
			}
		};
		
		selector.configure(resourceTypeSelector);
		
		Set<ResourceType> types = resourceTypeSelector.getResourceTypes();
		
		assertTrue(types.size() == 1 && types.contains(javaClasses()));
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testEmptyFrom() {
		ComponentSelector selector = new ComponentQuery() {
			protected void query() {
				select().from();
			}
		};
		
		selector.configure(resourceTypeSelector);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testEmptyStringFrom() {
		ComponentSelector selector = new ComponentQuery() {
			protected void query() {
				select().from("");
			}
		};
		
		selector.configure(resourceTypeSelector);
	}
	
	@Test(dataProviderClass = TestDataProvider.class, dataProvider = "validPackages")
	public void testValidPackagesFrom(final String packageA, final String packageB, final String packageC) {
		ComponentSelector selector = new ComponentQuery() {
			protected void query() {
				select().from(packageA, packageB, packageC);
			}
		};
		
		selector.configure(resourceTypeSelector);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testEmptyAndStore() {
		ComponentSelector selector = new ComponentQuery() {
			protected void query() {
				select().from("net.sf").andStore();
			}
		};
		
		selector.configure(resourceTypeSelector);
	}
}
