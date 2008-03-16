package jconch.pipeline;

import org.apache.commons.lang.NullArgumentException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The base interface for a stage in the pipeline.
 *
 * @author Robert Fischer
 */
public abstract class PipeStage implements PipeElement {

    private final ThreadingModel threads;

    /**
     * Have we started yet?
     */
    private final AtomicBoolean started = new AtomicBoolean(false);

    /**
     * Constructor.
     *
     * @param threading The threading approach.
     * @throws NullArgumentException If the argument is <code>null</code>
     */
    protected PipeStage(final ThreadingModel threading) {
        if (threading == null) {
            throw new NullArgumentException("threading");
        }
        threads = threading;
    }

    /**
     * Starts the pipeline within its threading model. A pipline stage can only
     * be started once; subsequent calls to this method do nothing.
     */
    public void start() {
        if (!started.getAndSet(true)) {
            threads.execute(this);
        } else {
            logMessage("Can only start once", new IllegalStateException("Called #start() when already started"));
        }
    }

    /**
     * Whether this pipline stage has been started or not.
     *
     * @return If we have already started this pipeline stage.
     */
    public boolean isStarted() {
        return started.get();
    }

    /**
     * Executes one round of processing for this pipeline stage.
     *
     * @throws IllegalStateException If there is nothing left to execute.
     */
    public abstract void execute();

    /**
     * If the pipeline is not supposed to handle any more elements, either
     * because of an error or because a producer is exhausted.
     *
     * @return If the pipeline is done producing/consuming elements.
     */
    public abstract boolean isFinished();

    /**
     * Called when an exception occurs in execution.
     *
     * @param msg The message describing the problem (may be <code>null</code>)
     * @param e   The exception which is the problem.
     */
    public abstract void logMessage(final String msg, final Exception e);

    /**
     * Gets the threading model of the instance.
     *
     * @return the threading model
     */
    public ThreadingModel getThreadingModel() {
        return threads;
    }
}
