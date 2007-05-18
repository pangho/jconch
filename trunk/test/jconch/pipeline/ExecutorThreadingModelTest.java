package jconch.pipeline;

import static org.junit.Assert.*;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.time.DateUtils;
import org.easymock.classextension.EasyMock;
import org.junit.Test;

public class ExecutorThreadingModelTest {

    @Test(expected = NullArgumentException.class)
    public void constructorExplodesOnNull() {
        new ExecutorThreadingModel(null);
    }

    @Test(expected = NullArgumentException.class)
    public void executeExplodesOnNull() {
        new ExecutorThreadingModel(EasyMock.createMock(Executor.class)).execute(null);
    }

    @Test
    public void executorCalledToExecuteThings() {
        final DirectTallyExecutor exec = new DirectTallyExecutor();
        final ExecutorThreadingModel fixture = new ExecutorThreadingModel(exec);
        final int rounds = 10;
        final ThreadCapturingPipelineStage doMe = new ThreadCapturingPipelineStage(fixture, 10);
        doMe.start();
        for (int i = 0; i < rounds * 2; i++) {
            if (doMe.isFinished()) {
                break;
            }
            try {
                Thread.sleep(DateUtils.MILLIS_PER_SECOND / 10);
            } catch (InterruptedException e) {
                Thread.yield();
            }
        }
        assertTrue("Did not finish", doMe.isFinished());
        assertEquals("Executor did not get called proper number of times", rounds, exec.count.get());
        assertEquals("Did not capture right number of threads", rounds, doMe.executeThreads.size());
        final Thread base = doMe.executeThreads.get(0);
        assertNotNull("Base thread was null", base);
        for (final Thread t : doMe.executeThreads) {
            assertSame("Executed in different thread", base, t);
        }
    }

    private static final class DirectTallyExecutor implements Executor {

        public final AtomicInteger count = new AtomicInteger(0);

        public void execute(final Runnable command) {
            count.incrementAndGet();
            command.run();
        }
    }
}
