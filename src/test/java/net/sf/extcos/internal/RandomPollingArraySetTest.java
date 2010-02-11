package net.sf.extcos.internal;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author  Matthias
 */
public class RandomPollingArraySetTest {
    private RandomPollingArraySet<Comparable<?>> set;
	
	@BeforeClass
	public void initTests(){
		set = new RandomPollingArraySet<Comparable<?>>();
	}
	
	@Test(invocationCount = 100)
	public void testPollRandom() {
		List<Comparable<?>> list = new ArrayList<Comparable<?>>();
		Random prng = new Random();
		int elements = (int)(prng.nextDouble() * 50);
		
		for (int i = 0; i < elements; i++) {
			list.add(i);
		}
		
		set.addAll(list);
		
		for (int i = 0; i < list.size(); i++) {
			assertTrue(list.contains(set.pollRandom()));
		}
		
		assertTrue(set.size() == 0);
	}
}