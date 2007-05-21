package jconch.pipeline;

import jconch.pipeline.impl.InlineThreadingModel;

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
        final ThreadCapturingPipelineStage stage = new ThreadCapturingPipelineStage(fixture, 10);
        stage.start();
        assertTrue("Did not finish inline", stage.isFinished());
        for (final Thread t : stage.executeThreads) {
            assertSame("Thread is different", Thread.currentThread(), t);
        }
    }
}
