package jconch.pipeline;

import static org.testng.AssertJUnit.*;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.lang.NullArgumentException;
import org.testng.annotations.Test;

public class BasePipeLinkTest extends AbstractPipeLinkTest {

	@Override
	protected PipeLink createFixture() {
		return new PipeLink<Object>(new ArrayBlockingQueue<Object>(this
				.getMaxCapacity()));
	}

	@Override
	protected int getMaxCapacity() {
		return 1000;
	}

	@Test
	public void junit4NeedsToNoticeThisIsATest() {
		assertTrue(true);
	}

	@Test(expectedExceptions = NullArgumentException.class)
	public void pipeLinkConstructorExplodesOnNullArg() {
		new PipeLink<Object>(null);
	}

}
