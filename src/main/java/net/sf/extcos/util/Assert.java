/*
 * Copyright 2009, The RedRoo Development Team
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

import java.util.Collection;
import java.util.Map;

/**
 * This class provides several static assertion methods for common use.
 * 
 * @author Matthias Rothe
 */
public class Assert {

    private Assert() {
    } // so it can't be instantiated

    
    /**
     * Asserts that a given collection contains only instances of the given
     * value type. Throws an exception of the given type if the assertion fails.
     * 
     * @param collection
     * 			  The collection to be asserted to only contain instances of
     * 			  the given value type.
     * @param valueType
     * 			  The value type all instances of the collection should be of
     * @param exceptionType
     * 			  The type of the exception to be thrown if the assertion fails
     */
    public static void containsOnly(Collection<?> collection, Class<?> valueType, Class<? extends RuntimeException> exceptionType) {
    	containsOnly(collection, valueType, exceptionType, null);
    }
    
    
    /**
     * Asserts that a given collection contains only instances of the given
     * value type. Throws an exception of the given type having the given
     * message if the assertion fails.
     * 
     * @param collection
     * 			  The collection to be asserted to only contain instances of
     * 			  the given value type.
     * @param valueType
     * 			  The value type all instances of the collection should be of
     * @param exceptionType
     * 			  The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void containsOnly(Collection<?> collection, Class<?> valueType, Class<? extends RuntimeException> exceptionType, String message) {
		notEmpty(collection, exceptionType, message);
    	for (Object object : collection) {
			instanceOf(object, valueType, exceptionType, message);
		}
    }
    
    
    /**
     * Asserts that the given integer a is greater than the given integer b.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param a
     *            integer a
     * @param b
     *            integer b
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void greaterThan(int a, int b, Class<? extends RuntimeException> exceptionType) {
    	greaterThan(a, b, exceptionType, null);
    }
    
    
    /**
     * Asserts the the given integer a is greater than the given integer b.
     * Throws an exception of the given type having the given message if the
     * assertion fails.
     * 
     * @param a
     *            integer a
     * @param b
     *            integer b
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void greaterThan(int a, int b, Class<? extends RuntimeException> exceptionType, String message) {
    	if(a <= b)
    		throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given object a is identical to the given object b.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param a
     *            Object a
     * @param b
     *            Object b
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void identical(Object a, Object b, Class<? extends RuntimeException> exceptionType) {
    	identical(a, b, exceptionType, null);
    }


    /**
     * Asserts that the given object a is identical to the given object b.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param a
     *            Object a
     * @param b
     *            Object b
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void identical(Object a, Object b, Class<? extends RuntimeException> exceptionType, String message) {
        if (a != b)
            throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given object is an instance of the given class or of or of one of its
     * subclasses. Throws an exception of the given type if the assertion fails.
     * 
     * @param o
     *            The object for which the assertion works
     * @param c
     *            The class the given object is asserted against
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void instanceOf(Object o, Class<?> c, Class<? extends RuntimeException> exceptionType) {
    	instanceOf(o, c, exceptionType, null);
    }


    /**
     * Asserts that the given object is an instance of the given class or of or of one of its
     * subclasses. Throws an exception of the given type having the given message if the
     * assertion fails.
     * 
     * @param o
     *            The object for which the assertion works
     * @param c
     *            The class the given object is asserted against
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void instanceOf(Object o, Class<?> c, Class<? extends RuntimeException> exceptionType, String message) {
    	if (o == null || c == null || !c.isAssignableFrom(o.getClass()))
        	throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given boolean is false.
     * Throws an exception of the given type if
     * the assertion fails.
     * 
     * @param b
     *            The boolean to be asserted for being false
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void isFalse(boolean b, Class<? extends RuntimeException> exceptionType) {
    	isFalse(b, exceptionType, null);
    }


    /**
     * Asserts that the given boolean is false.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param b
     *            The boolean to be asserted for being false
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void isFalse(boolean b, Class<? extends RuntimeException> exceptionType, String message) {
        if (b)
            throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given object is null.
     * Throws an exception of the given type if
     * the assertion fails.
     * 
     * @param o
     *            The object to be asserted for being null
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void isNull(Object o, Class<? extends RuntimeException> exceptionType) {
    	isNull(o, exceptionType, null);
    }


    /**
     * Asserts that the given object is null.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param o
     *            The object to be asserted for being null
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void isNull(Object o, Class<? extends RuntimeException> exceptionType, String message) {
        if (o != null)
            throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given boolean is true.
     * Throws an exception of the given type if
     * the assertion fails.
     * 
     * @param b
     *            The boolean to be asserted for being true
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void isTrue(boolean b, Class<? extends RuntimeException> exceptionType) {
    	isTrue(b, exceptionType, null);
    }


    /**
     * Asserts that the given boolean is true.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param b
     *            The boolean to be asserted for being true
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void isTrue(boolean b, Class<? extends RuntimeException> exceptionType, String message) {
        if (!b)
            throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given array is not null and not empty.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param array
     *            The array to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static <T> void notEmpty(T[] array, Class<? extends RuntimeException> exceptionType) {
    	notEmpty(array, exceptionType, null);
    }
    
    
    /**
     * Asserts that the given array is not null and not empty.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param array
     *            The array to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static <T> void notEmpty(T[] array, Class<? extends RuntimeException> exceptionType, String message) {
    	if (array == null || array.length == 0)
    		throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given collection is not null and not empty.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param collection
     *            The collection to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void notEmpty(Collection<?> collection, Class<? extends RuntimeException> exceptionType) {
    	notEmpty(collection, exceptionType, null);
    }


    /**
     * Asserts that the given collection is not null and not empty.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param collection
     *            The collection to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void notEmpty(Collection<?> collection, Class<? extends RuntimeException> exceptionType, String message) {
        if (collection == null || collection.isEmpty())
            throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given map is not null and not empty.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param map
     *            The map to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void notEmpty(Map<?, ?> map, Class<? extends RuntimeException> exceptionType) {
    	notEmpty(map, exceptionType, null);
    }
    
    
    /**
     * Asserts that the given map is not null and not empty.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param map
     *            The map to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void notEmpty(Map<?, ?> map, Class<? extends RuntimeException> exceptionType, String message) {
    	if (map == null || map.isEmpty())
    		throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given string is not null and not the empty string.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param string
     *            The string to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void notEmpty(String string, Class<? extends RuntimeException> exceptionType) {
    	notEmpty(string, exceptionType, null);
    }


    /**
     * Asserts that the given string is not null and not the empty string.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param string
     *            The string to be asserted for not being null or empty
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void notEmpty(String string, Class<? extends RuntimeException> exceptionType, String message) {
        if (string == null || string.equals(""))
            throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Asserts that the given object is not null.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param o
     *            The object to be asserted for not being null
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void notNull(Object o, Class<? extends RuntimeException> exceptionType) {
    	notNull(o, exceptionType, null);
    }


    /**
     * Asserts that the given object is not null.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param o
     *            The object to be asserted for not being null
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void notNull(Object o, Class<? extends RuntimeException> exceptionType, String message) {
        if (o == null)
            throw createException(exceptionType, message, null);
    }
    
    
    public static void state(boolean state) {
    	if (!state)
    		throw new IllegalStateException();
    }
    
    
    public static void state(boolean state, String message) {
    	if (!state)
    		throw new IllegalStateException(message);
    }
    
    
    /**
     * Asserts that the given class a is a subclass of the given class b.
     * Throws an exception of the given type if the assertion fails.
     * 
     * @param a
     *            Class a
     * @param b
     *            Class b
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     */
    public static void subclassOf(Class<?> a, Class<?> b, Class<? extends RuntimeException> exceptionType) {
    	subclassOf(a, b, exceptionType, null);
    }

    
    /**
     * Asserts that the given class a is a subclass of the given class b.
     * Throws an exception of the given type having the given message if
     * the assertion fails.
     * 
     * @param a
     *            Class a
     * @param b
     *            Class b
     * @param exceptionType
     *            The type of the exception to be thrown if the assertion fails
     * @param message
     *            The message for the exception
     */
    public static void subclassOf(Class<?> a, Class<?> b, Class<? extends RuntimeException> exceptionType, String message) {
    	if (a == null || b == null || !b.isAssignableFrom(a))
    		throw createException(exceptionType, message, null);
    }
    
    
    /**
     * Shorthand for writing IllegalArgumentException.class
     * 
     * @return Class&lt;IllegalArgumentException&gt;
     */
    public static Class<IllegalArgumentException> iae() {
    	return IllegalArgumentException.class;
    }
    
    
    /**
     * Shorthand for writing IllegalStateException.class
     * 
     * @return Class&lt;IllegalStateException&gt;
     */
    public static Class<IllegalStateException> ise() {
    	return IllegalStateException.class;
    }
    
    
    /**
     * Shorthand for writing AssertionError.class
     * 
     * @return Class&lt;AssertionError&gt;
     */
    public static Class<AssertionError> ae() {
    	return AssertionError.class;
    }
    
    
    /**
     * Creates a new exception of the given type or a new
     * {@link RuntimeException} if no instance of the given type can be
     * created. Sets the given message and cause on the newly created
     * exception instance. Returns that instance.
     * 
     * @param exceptionType
     * 			  The type of which to create a new exception instance
     * @param message
     * 			  The message to set on the exception instance
     * @param cause
     * 			  The cause to set on the exception instance
     * @return The created exception instance with message and cause set
     */
    private static RuntimeException createException(Class<? extends RuntimeException> exceptionType, String message, Throwable cause) {
    	RuntimeException re;
    	
    	try {
    		re = exceptionType.newInstance();
    	} catch (Exception e) {
    		re = new RuntimeException();
    	}
    	
    	if (message != null || cause != null) {
            try {
                PropertyInjector injector = new PropertyInjector();
                injector.setTarget(re);
                
                if (message != null) {
                	injector.inject("detailMessage", message);
                }
                
                if (cause != null) {
                    injector.inject("cause", cause);
                }
            } catch (NoSuchFieldException ignored) {
                // should never happen
            }
    	}
    	
    	return re;
    }
}