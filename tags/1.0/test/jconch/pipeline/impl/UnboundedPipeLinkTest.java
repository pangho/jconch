package jconch.pipeline.impl;

import static org.testng.AssertJUnit.*;
import jconch.pipeline.AbstractPipeLinkTest;

import org.testng.annotations.Test;

public class UnboundedPipeLinkTest extends
		AbstractPipeLinkTest<UnboundedPipeLink<Object>> {

	@Override
	protected UnboundedPipeLink<Object> createFixture() {
		return new UnboundedPipeLink<Object>();
	}

	@Override
	protected int getMaxCapacity() {
		return Integer.MAX_VALUE;
	}

	@Test
	public void thisIsAJUnitTestReally() {
		assertTrue(true);
	}
}
