package dst.ass1.jpa;

import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.lifecycle.EMLifecycleDemo;

public class Test_4a extends AbstractTest {

	private EMLifecycleDemo demo;

	@Before
	public void initDemo() {
		demo = new EMLifecycleDemo(em, modelFactory);
	}

	@Test
	public void testEMLifecycleDemo() {
		try {
			demo.demonstrateEntityMangerLifecycle();
		} catch (Exception e) {
			fail("No Exception expected but got: " + e.getMessage());
		}
	}
}
