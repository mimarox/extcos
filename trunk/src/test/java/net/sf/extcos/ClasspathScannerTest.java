package net.sf.extcos;

import java.util.Set;

import net.sf.extcos.internal.ArraySet;

import org.testng.annotations.Test;

import com.matthiasrothe.TestInterface;

public class ClasspathScannerTest {
	@Test
	public void testGetClasses() {
		ClasspathScanner scanner = new ClasspathScanner();
		
		final Set<Class<? extends TestInterface>> store = new ArraySet<Class<? extends TestInterface>>();
		Set<Class<?>> classes = scanner.getClasses(new AbstractClassSelector() {
			protected void query() {
				select().
				from("com").
				andStore(thoseImplementing(TestInterface.class).into(store)).
				returning(allExtending(Object.class));
			}
		});
		
		System.out.println(store);
		System.out.println(classes);
	}
}