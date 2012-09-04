package unittests;

import static org.testng.Assert.*;

import net.sf.extcos.util.StringUtils;

import org.testng.annotations.Test;

public class StringUtilsTest {

	@Test
	public void testIsJavaPackageWithWildcards() {
		assertTrue(StringUtils.isJavaPackageWithWildcards("org"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("org.foo"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("org.foo.bar"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("org.*.foo"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("org.**.foo"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("org.*.foo.**.bar"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("org.**.foo.*.bar"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("*.foo.**"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("**.foo.*"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("*"));
		assertTrue(StringUtils.isJavaPackageWithWildcards("**"));
		
		assertFalse(StringUtils.isJavaPackageWithWildcards(""));
		assertFalse(StringUtils.isJavaPackageWithWildcards("4org"));
		assertFalse(StringUtils.isJavaPackageWithWildcards("org."));
		assertFalse(StringUtils.isJavaPackageWithWildcards("org.***.foo"));
	}

	@Test
	public void testIsJavaPackage() {
		assertTrue(StringUtils.isJavaPackage("org"));
		assertTrue(StringUtils.isJavaPackage("org.foo"));
		assertTrue(StringUtils.isJavaPackage("org.foo.bar"));
		
		assertFalse(StringUtils.isJavaPackage(""));
		assertFalse(StringUtils.isJavaPackage("4org"));
		assertFalse(StringUtils.isJavaPackage("org."));
		assertFalse(StringUtils.isJavaPackage("org.***.foo"));
		assertFalse(StringUtils.isJavaPackage("org.*.foo"));
		assertFalse(StringUtils.isJavaPackage("org.**.foo"));
		assertFalse(StringUtils.isJavaPackage("org.*.foo.**.bar"));
		assertFalse(StringUtils.isJavaPackage("org.**.foo.*.bar"));
		assertFalse(StringUtils.isJavaPackage("*.foo.**"));
		assertFalse(StringUtils.isJavaPackage("**.foo.*"));
		assertFalse(StringUtils.isJavaPackage("*"));
		assertFalse(StringUtils.isJavaPackage("**"));
	}
}
