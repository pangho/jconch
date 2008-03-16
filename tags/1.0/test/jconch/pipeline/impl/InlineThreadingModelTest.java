package jconch.pipeline.impl;

import static org.testng.AssertJUnit.*;

import org.apache.commons.lang.NullArgumentException;
import org.testng.annotations.Test;

public class InlineThreadingModelTest {

	@Test
	public void executeCalledOnSameThread() {
		final InlineThreadingModel fixture = new InlineThreadingModel();
		final ThreadCapturingPipelineStage stage = new ThreadCapturingPipelineStage(
				fixture, 10);
		stage.start();
		assertTrue("Did not finish inline", stage.isFinished());
		for (final Thread t : stage.executeThreads) {
			assertSame("Thread is different", Thread.currentThread(), t);
		}
	}

	@Test(expectedExceptions = NullArgumentException.class)
	public void executeExplodesOnNull() {
		new InlineThreadingModel().execute(null);
	}
}
