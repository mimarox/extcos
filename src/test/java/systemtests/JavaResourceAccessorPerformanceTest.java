package systemtests;

import java.util.Set;

import net.sf.extcos.resource.Resource;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import common.StopWatch;
import common.TestBase;

public class JavaResourceAccessorPerformanceTest extends TestBase {
	private ClassGenerationAndUseTest testProvider;
	
	@BeforeClass
	public void initTestProvider() throws NoSuchFieldException {
		testProvider = new ClassGenerationAndUseTest();
		testProvider.initResolver();
	}
	
	@SuppressWarnings("unchecked")
	@Test(invocationCount = 100, timeOut = 600, successPercentage = 95)
	public void testJavaResourceAccessorPerformance() {
		Set<Resource> resources = (Set<Resource>) testProvider.getResources()[0][0];
		StopWatch watch = new StopWatch();
		watch.start();
		
		for (int i = 0; i < 100; i++) {
			invokeAllTests(resources);
		}
		
		watch.stop();
		System.out.println("JavaResourceAccessor: " + watch.getDuration());
	}

	private void invokeAllTests(Set<Resource> resources) {
		initMethodCall();
		testProvider.testGetAnnotatedWithEnumBasedClasses(resources);
		
		initMethodCall();
		testProvider.testGetAnnotatedWithStateClasses(resources);
		
		initMethodCall();
		testProvider.testGetExtendingClasses(resources);
		
		initMethodCall();
		testProvider.testGetImplementingClasses(resources);
		
		try {
			initMethodCall();
			testProvider.testGetRootFilteredClasses(resources);
		} catch (Exception e) {}
	}

	private void initMethodCall() {
		testProvider.initClasses();
		testProvider.initClassLoader();
	}
}