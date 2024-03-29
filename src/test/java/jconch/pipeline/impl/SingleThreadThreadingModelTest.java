package jconch.pipeline.impl;

import static org.testng.AssertJUnit.*;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.time.DateUtils;
import org.testng.annotations.Test;

public class SingleThreadThreadingModelTest {

	@Test
	public void checkThatOneInstancesUsesDifferentSingleThreadsForTwoStages() {
		final SingleThreadThreadingModel fixture = new SingleThreadThreadingModel();
		final ThreadCapturingPipelineStage stage1 = new ThreadCapturingPipelineStage(
				fixture, 10);
		final ThreadCapturingPipelineStage stage2 = new ThreadCapturingPipelineStage(
				fixture, 10);
		stage1.start();
		stage2.start();
		try {
			Thread.sleep(2 * DateUtils.MILLIS_PER_SECOND);
		} catch (final InterruptedException e) {
			Thread.yield();
		}
		assertTrue("Stage 1 did not finish", stage1.isFinished());
		assertTrue("Stage 2 did not finish", stage2.isFinished());
		for (final Thread t1 : stage1.executeThreads) {
			for (final Thread t2 : stage2.executeThreads) {
				assertNotSame("Saw the same thread twice", t1, t2);
			}
		}
	}

	@Test
	public void checkToSeeWeAreInASingleThread() {
		final SingleThreadThreadingModel fixture = new SingleThreadThreadingModel();
		final ThreadCapturingPipelineStage stage = new ThreadCapturingPipelineStage(
				fixture, 10);
		stage.start();
		try {
			Thread.sleep(2 * DateUtils.MILLIS_PER_SECOND);
		} catch (final InterruptedException e) {
			Thread.yield();
		}
		assertTrue("Processing is not finished", stage.isFinished());
		for (final Thread t1 : stage.executeThreads) {
			for (final Thread t2 : stage.executeThreads) {
				assertSame("Saw a different thread", t1, t2);
			}
		}
	}

	@Test(expectedExceptions = NullArgumentException.class)
	public void executeExplodesOnNull() {
		new SingleThreadThreadingModel().execute(null);
	}
}
