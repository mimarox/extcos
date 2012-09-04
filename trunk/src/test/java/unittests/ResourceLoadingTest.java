package unittests;

import static org.testng.Assert.assertTrue;

import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.Test;

public class ResourceLoadingTest {

	@Test
	public void loadAllResources() throws Exception {
		Enumeration<URL> resources = getClass().getClassLoader().getResources("org/");
		assertTrue(resources.hasMoreElements());
		
		Set<URL> urls = new HashSet<URL>();
		
		while (resources.hasMoreElements()) {
			urls.add(resources.nextElement());
		}
		
		System.out.println(urls.size());
		System.out.println(urls);
		
		assertTrue(urls.size() > 0);
	}
}