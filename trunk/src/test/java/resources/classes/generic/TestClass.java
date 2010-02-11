package resources.classes.generic;

import resources.annotations.TestInvokable;

public class TestClass {
	
	@TestInvokable
	public void testMethod() {
		System.out.println("called TestClass.testMethod()");
	}
}
