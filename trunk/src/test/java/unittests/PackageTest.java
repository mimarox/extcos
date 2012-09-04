package unittests;

import static org.testng.Assert.*;

import java.util.regex.Pattern;

import org.testng.annotations.Test;
import net.sf.extcos.selector.Package;

public class PackageTest {

	@Test
	public void testPackages() {
		Package pckg = new Package("net.*.foo.**.bar");
		assertEquals(pckg.getBasePath(), "net/");
		
		Pattern pattern = Pattern.compile(pckg.getSubPathPattern());
		assertTrue(pattern.matcher("first/foo/second/third/bar/").matches());
		
		assertTrue("file://C:/files/!net/first/foo/second/third/bar/sample.class".matches(
				"^file://C:/files/!net/" + pckg.getSubPathPattern() + ".*$"));
	}
}
