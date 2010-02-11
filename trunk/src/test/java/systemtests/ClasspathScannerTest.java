package systemtests;

import java.util.Set;

import net.sf.extcos.AbstractClassSelector;
import net.sf.extcos.ClasspathScanner;
import net.sf.extcos.internal.ArraySet;

import org.testng.annotations.Test;

import resources.classes.generic.TestInterface;

import common.TestBase;


public class ClasspathScannerTest extends TestBase {
	@Test
	public void testGetClasses() {
		ClasspathScanner scanner = new ClasspathScanner();
		
		final Set<Class<? extends TestInterface>> store = new ArraySet<Class<? extends TestInterface>>();
		Set<Class<?>> classes = scanner.getClasses(new AbstractClassSelector() {
			protected void query() {
				select().
				from(getProperty("resources.package")).
				andStore(thoseImplementing(TestInterface.class).into(store)).
				returning(allExtending(Object.class));
			}
		});
		
		System.out.println(store);
		System.out.println(classes);
	}
}