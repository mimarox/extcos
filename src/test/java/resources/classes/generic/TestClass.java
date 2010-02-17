package resources.classes.generic;

import resources.annotations.TestInvokable;

public class TestClass {
	public ClassWithNestedClass clazz;
	
	@TestInvokable
	public void testMethod() {
		System.out.println("called TestClass.testMethod()");
	}
}
