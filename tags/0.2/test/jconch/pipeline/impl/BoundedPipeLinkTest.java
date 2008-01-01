package jconch.pipeline.impl;

import static org.testng.AssertJUnit.*;
import jconch.pipeline.AbstractPipeLinkTest;

import org.testng.annotations.Test;

public class BoundedPipeLinkTest extends
		AbstractPipeLinkTest<BoundedPipeLink<Object>> {

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void constructorExplodesOnNegativeCapacity() {
		new BoundedPipeLink<Object>(-1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void constructorExplodesOnZeroCapacity() {
		new BoundedPipeLink<Object>(0);
	}

	@Override
	protected BoundedPipeLink<Object> createFixture() {
		return new BoundedPipeLink<Object>(this.getMaxCapacity());
	}

	@Test
	public void getJUnit4ToSeeThisTest() {
		assertTrue(true);
	}

	@Override
	protected int getMaxCapacity() {
		return 10000;
	}

}
