package jconch.pipeline;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;
import static org.junit.Assert.*;

public class InlineThreadingModelTest {

	@Test(expected = NullArgumentException.class)
	public void executeExplodesOnNull() {
		new InlineThreadingModel().execute(null);
	}

	@Test
	public void executeCalledOnSameThread() {
		final InlineThreadingModel fixture = new InlineThreadingModel();
		final ThreadCapturingPipelineStage stage = new ThreadCapturingPipelineStage(
				fixture);
		stage.execute();
		assertSame("Thread is different", Thread.currentThread(), stage.executeThread);
	}
}
