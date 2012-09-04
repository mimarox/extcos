package unittests;

import static net.sf.extcos.internal.JavaClassResourceType.javaClasses;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import net.sf.extcos.ComponentQuery;
import net.sf.extcos.internal.ArraySet;
import net.sf.extcos.selector.BasePackageSelector;
import net.sf.extcos.selector.ForwardingBuilder;
import net.sf.extcos.selector.ResourceTypeSelector;
import net.sf.extcos.spi.ResourceType;
import net.sf.extcos.util.PropertyInjector;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import resources.annotations.First;

import unittests.beans.PackagePatternDefinition;

public class ComponentQueryTest {

	ResourceTypeSelector resourceTypeSelector;

	@BeforeMethod
	public void init() throws Exception {
		resourceTypeSelector = new ResourceTypeSelector();
		BasePackageSelector basePackageSelector = new BasePackageSelector();

		PropertyInjector injector = new PropertyInjector();
		injector.setTarget(resourceTypeSelector);
		injector.inject("basePackageSelector", basePackageSelector);

		injector.setTarget(basePackageSelector);
		injector.inject("forwardingBuilder", new ForwardingBuilder());
		injector.inject("basePackages", new ArraySet<Package>());
	}

	@Test
	public void testEmptySelect() {
		ComponentQuery selector = new ComponentQuery() {
			@Override
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
		ComponentQuery selector = new ComponentQuery() {
			@Override
			protected void query() {
				select().from();
			}
		};

		selector.configure(resourceTypeSelector);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testEmptyStringFrom() {
		ComponentQuery selector = new ComponentQuery() {
			@Override
			protected void query() {
				select().from("");
			}
		};

		selector.configure(resourceTypeSelector);
	}

	@Test(dataProviderClass = TestDataProvider.class, dataProvider = "validPackages")
	public void testValidPackagesFrom(final String packageA, final String packageB, final String packageC) {
		ComponentQuery selector = new ComponentQuery() {
			@Override
			protected void query() {
				select().from(packageA, packageB, packageC);
			}
		};

		selector.configure(resourceTypeSelector);
	}

	@Test(dataProviderClass = TestDataProvider.class, dataProvider = "validPackagePatternDefinitions")
	public void testValidPackagePatternDefinitionsFrom(final PackagePatternDefinition[] definitions) {
		ComponentQuery selector = new ComponentQuery() {
			@Override
			protected void query() {
				if (definitions.length == 1) {
					select().from(
							allSubPackages(definitions[0].getSubPackagePattern()).
							in(definitions[0].getRootPackages()));
				} else {
					ArrayList<String[]> packagePatterns = new ArrayList<String[]>();
					
					for (PackagePatternDefinition definition : definitions) {
						packagePatterns.add(allSubPackages(definition.getSubPackagePattern()).
								in(definition.getRootPackages()));
					}
					
					select().from(join(packagePatterns.toArray(new String[][]{})));
				}
			}
		};

		selector.configure(resourceTypeSelector);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testEmptyAndStore() {
		ComponentQuery selector = new ComponentQuery() {
			@Override
			protected void query() {
				select().from("net.sf").andStore();
			}
		};

		selector.configure(resourceTypeSelector);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testNullReturningAfterAndStore() {
		ComponentQuery selector = new ComponentQuery() {
			@Override
			protected void query() {
				Set<Class<?>> firstStore = new HashSet<Class<?>>();
				select().from("net.sf").andStore(thoseAnnotatedWith(First.class).into(firstStore)).returning(null);
			}
		};

		selector.configure(resourceTypeSelector);
	}
	
	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testNullReturning() {
		ComponentQuery selector = new ComponentQuery() {
			@Override
			protected void query() {
				select().from("net.sf").returning(null);
			}
		};

		selector.configure(resourceTypeSelector);
	}
}
