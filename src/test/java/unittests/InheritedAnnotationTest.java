package unittests;

import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import resources.all.test.classes.in.use.annotated.inherited.InheritedAnnotationClass;
import resources.annotations.First;

public class InheritedAnnotationTest {
	@Test
	public void testHasInheritedAnnotation() {
		Class<InheritedAnnotationClass> clazz = InheritedAnnotationClass.class;
		assertNotNull(clazz.getAnnotation(First.class));
	}
}
