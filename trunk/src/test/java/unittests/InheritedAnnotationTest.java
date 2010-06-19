package unittests;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import resources.annotations.First;
import resources.classes.annotated.inherited.InheritedAnnotationClass;

public class InheritedAnnotationTest {
	@Test
	public void testHasInheritedAnnotation() {
		Class<InheritedAnnotationClass> clazz = InheritedAnnotationClass.class;
		assertNotNull(clazz.getAnnotation(First.class));
	}
}
