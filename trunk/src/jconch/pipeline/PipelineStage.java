package jconch.pipeline;

import org.apache.commons.lang.NullArgumentException;

/**
 * The base interface for a stage in the pipeline.
 * 
 * @author Robert Fischer
 */
public abstract class PipelineStage {

    private final ThreadingModel threads;

    /**
     * Constructor.
     * 
     * @param threading
     *            The threading approach.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    protected PipelineStage(final ThreadingModel threading) {
        if (threading == null) {
            throw new NullArgumentException("threading");
        }
        threads = threading;
    }

    /**
     * Starts the pipeline within its threading model.
     */
    public void start() {
        threads.execute(this);
    }

    /**
     * Executes one round of processing for this pipeline stage.
     * 
     * @throws IllegalStateException
     *             If there is nothing left to execute.
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
     * @param msg
     *            The message describing the problem (may be <code>null</code>)
     * @param e
     *            The exception which is the problem.
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
