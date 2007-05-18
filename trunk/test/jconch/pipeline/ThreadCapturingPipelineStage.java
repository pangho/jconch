package jconch.pipeline;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * A pipeline stage that simply captures the thread that executed it. Useful in
 * unit tests to catch threading models.
 * 
 * @author Robert Fischer
 */
class ThreadCapturingPipelineStage extends PipelineStage {

    public final List<Thread> executeThreads;

    public final int rounds;

    public ThreadCapturingPipelineStage(final ThreadingModel threading, final int rounds) {
        super(threading);
        this.executeThreads = ListUtils.typedList(new ArrayList(rounds + 1), Thread.class);
        this.rounds = rounds;
    }

    @Override
    void execute() {
        executeThreads.add(Thread.currentThread());
        try {
            Thread.sleep(DateUtils.MILLIS_PER_SECOND / 10);
        } catch (InterruptedException e) {
            Thread.yield();
        }
    }

    @Override
    protected void logMessage(String msg, Exception e) {
        // Does nothing
    }

    @Override
    public boolean isFinished() {
        return executeThreads.size() == rounds;
    }

}
