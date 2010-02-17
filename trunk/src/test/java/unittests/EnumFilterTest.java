package unittests;

import static org.testng.Assert.assertTrue;

import java.io.IOException;

import org.objectweb.asm.ClassReader;
import org.testng.annotations.Test;

public class EnumFilterTest {
	
	@Test
	public void testEnumSupertype() throws IOException {
		ClassReader reader = new ClassReader(getClass().
				getResourceAsStream("../resources/enums/MyTestEnum$1.class"));
		
		System.out.println("Super Name: " + reader.getSuperName());
		System.out.println("Class Name: " + reader.getClassName());
		
		assertTrue(reader.getSuperName().equals("java/lang/Enum") ||
				reader.getClassName().matches(".*Enum\\$[0-9]+"));
	}
}
