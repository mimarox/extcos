package unittests;

import static org.testng.Assert.assertEquals;
import net.sf.extcos.util.ArrayUtils;

import org.testng.annotations.Test;

public class ArrayUtilsTest {
	@Test
	public void testJoin() {
		String[] expected = {"a", "b", "c", "d", "e", "f"};
		String[] actual = ArrayUtils.join(new String[]{"a", "b", "c"}, new String[]{}, new String[]{"d"}, new String[]{"e", "f"});
		assertEquals(actual, expected);
	}
	
	@Test
	public void testJoinEmpty() {
		assertEquals(ArrayUtils.join(), new Object[]{});
	}
}
