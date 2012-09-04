/*
 * Copyright 2009, The original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sf.extcos.util;

/**
 * This class provides utility methods to work with {@link String}s.
 * 
 * @author Matthias Rothe
 * @since RedRoo 1.0
 */
public class StringUtils {
	public static final String SINGLE_SUBPACKAGE_PATTERN = "[_$a-zA-Z][_$a-zA-Z0-9]*";
	
	private StringUtils() {
	} // so it can't be instantiated

	/**
	 * Concatenates all the given strings in the order they are given using
	 * {@link StringBuffer#append(String)} and returns the resulting string.
	 * 
	 * @param strings
	 *            The strings to be concatenated
	 * @return The resulting string
	 */
	public static String append(final String... strings) {
		StringBuffer result = new StringBuffer();

		for (String string : strings) {
			result.append(string);
		}

		return result.toString();
	}

	/**
	 * Concatenates all the string representations of the given objects in the
	 * order they are given using {@link StringBuffer#append(Object)} and
	 * returns the resulting string.
	 * 
	 * @param objects
	 *            The objects the string representations of which are to be
	 *            concatenated
	 * @return The resulting string
	 */
	public static String append(final Object... objects) {
		StringBuffer result = new StringBuffer();

		for (Object object : objects) {
			result.append(object);
		}

		return result.toString();
	}

	/**
	 * Checks whether the given string is a valid Java package name, according
	 * to the Java Language Specification.
	 * 
	 * @param string
	 *            The string to check
	 * @return true, if the string is a valid Java package name, false otherwise
	 */
	public static boolean isJavaPackage(final String string) {
		return string.matches(packagePattern(false));
	}

	/**
	 * Checks whether the given string is a valid Java package name, according
	 * to the Java Language Specification, or validly extends that by using wildcards
	 * for subpackage names.
	 * <p>
	 * There are two options for using wildcards.
	 * <ol>
	 * <li>Using a single asterisk (*) for masking exactly one subpackage. An
	 * example for this would be <code>org.sample.*.foo</code>. Matching packages
	 * would include <code>org.sample.first.foo</code> and
	 * <code>org.sample.second.foo</code>. However, a package
	 * <code>org.sample.first.second.foo</code> would not match.<br>&nbsp;</li>
	 * <li>Using a double asterisk (**) for masking arbitrarily many subpackages. An
	 * example for this would be <code>org.sample.**.foo</code>. Matching packages
	 * would include <code>org.sample.first.foo</code> and
	 * <code>org.sample.first.second.foo</code> and so on.</li>
	 * </ol>
	 * Both options can be combined for more fine grained control. Strings like
	 * <code>org.*.foo.**.bar</code> or <code>org.**.foo.*.bar</code> are therefore deemed
	 * to be valid as well.
	 * <p>
	 * <b>Note:</b> Using wildcards within subpackage names like in
	 * <code>org.sampl*.foo</code> is invalid.
	 * 
	 * @param string
	 *            The string to check
	 * @return true, if the string is a valid Java package name, false otherwise
	 */
	public static boolean isJavaPackageWithWildcards(final String string) {
		return string.matches(packagePattern(true));
	}
	
	private static String packagePattern(boolean withWildcards) {
		String identifierPattern = withWildcards ? "(?:(?:" + SINGLE_SUBPACKAGE_PATTERN + ")|\\*{1,2})" : SINGLE_SUBPACKAGE_PATTERN;
		String separator = "\\.";
		return append(identifierPattern, "(?:", separator, identifierPattern, ")*");
	}

	public static char fileSeparator() {
		return System.getProperty("file.separator").charAt(0);
	}

	/**
	 * Replace all occurences of a substring within a string with
	 * another string.
	 * @param inString String to examine
	 * @param oldPattern String to replace
	 * @param newPattern String to insert
	 * @return a String with the replacements
	 */
	public static String replace(final String inString, final String oldPattern, final String newPattern) {
		if (!hasLength(inString) || !hasLength(oldPattern) || newPattern == null) {
			return inString;
		}
		StringBuffer sbuf = new StringBuffer();
		// output StringBuffer we'll build up
		int pos = 0; // our position in the old string
		int index = inString.indexOf(oldPattern);
		// the index of an occurrence we've found, or -1
		int patLen = oldPattern.length();
		while (index >= 0) {
			sbuf.append(inString.substring(pos, index));
			sbuf.append(newPattern);
			pos = index + patLen;
			index = inString.indexOf(oldPattern, pos);
		}
		sbuf.append(inString.substring(pos));
		// remember to append any characters to the right of a match
		return sbuf.toString();
	}

	/**
	 * Check that the given CharSequence is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a CharSequence that purely consists of whitespace.
	 * <p><pre>
	 * StringUtils.hasLength(null) = false
	 * StringUtils.hasLength("") = false
	 * StringUtils.hasLength(" ") = true
	 * StringUtils.hasLength("Hello") = true
	 * </pre>
	 * @param str the CharSequence to check (may be <code>null</code>)
	 * @return <code>true</code> if the CharSequence is not null and has length
	 */
	public static boolean hasLength(final CharSequence str) {
		return str != null && str.length() > 0;
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of whitespace.
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasLength(CharSequence)
	 */
	public static boolean hasLength(final String str) {
		return hasLength((CharSequence) str);
	}
}