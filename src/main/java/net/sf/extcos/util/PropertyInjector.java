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

import java.lang.reflect.Field;

/**
 * This class injects property references into a target object.
 * 
 * @author Matthias Rothe
 * @since RedRoo 1.0
 */
public class PropertyInjector {
	private Object target;
	
	/**
	 * Injects a property reference into the currently set target assigning
	 * it to the named field. The field may either be declared in the currently
	 * set target's own class or in a superclass thereof.
	 * 
	 * @param name  the name of the field
	 * @param value the property reference to inject
	 * @throws NoSuchFieldException if the injection did not succeed, because the target doesn't have
	 *             a field with the given name or the value's type didn't match
	 * @throws IllegalStateException if the target has not been set
	 * @throws IllegalArgumentException if the name or the value is null, or the name is the empty string
	 */
	public void inject(String name, Object value) throws NoSuchFieldException {
		if(target == null){
			throw new IllegalStateException("The target has not been set");
		}
		
		if(name == null || name.length() == 0){
		    throw new IllegalArgumentException("The name must have one character at least");
		}
		
		if(value == null){
			throw new IllegalArgumentException("The value must not be null");
		}
		
		Class<?> clazz = target.getClass();
		
		while(clazz != null){
	        Field[] fields = clazz.getDeclaredFields();
	        
	        for(Field field : fields){
	            if(field.getName().equals(name)){
	                try {
	                    boolean accessible = field.isAccessible();
	                    
	                    field.setAccessible(true);
	                    field.set(target, value);
	                    field.setAccessible(accessible);
	                    
	                    return;
	                }
	                catch(Exception e){}
	            }
	        }
	        
	        clazz = clazz.getSuperclass();
		}
		
		throw new NoSuchFieldException("For the current target " + target +
		        " no property called " + name + " could be found for which the "
		        + "reference " + value + " could be set");
	}
	
	/**
	 * Sets the target for property injections
	 * 
	 * @param target the target to set
	 */
	public void setTarget(Object target) {
		this.target = target;
	}
	
}
