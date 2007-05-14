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
	 * Starts the pipeline.
	 */
	public void start() {
		threads.execute(this);
	}

	/**
	 * Executes the pipeline processing.
	 * 
	 * @throws IllegalStateException
	 *             If there is nothing left to execute.
	 */
	abstract void execute();

	/**
	 * Determines if the pipeline stage is exhausted.
	 * 
	 * @return If the stage is exhausted.
	 */
	abstract boolean isExhausted();

	/**
	 * <p>
	 * If the pipeline is not supposed to create any more elements. This
	 * condition is true if {@link #isExhausted()} returns true, or if an error
	 * condition has been detected.
	 * </p>
	 * <p>
	 * <b>Inheritor's Note:</b> This method should be overriden to check error
	 * conditions. This method simply checks for {@link #isExhausted()}.
	 * </p>
	 * 
	 * @return If the pipeline is done creating elements.
	 */
	public boolean isFinished() {
		return isExhausted();
	}

	/**
	 * Called when an exception occurs in execution.
	 * 
	 * @param msg
	 *            The message describing the problem (may be <code>null</code>)
	 * @param e
	 *            The exception which is the problem.
	 */
	protected abstract void logMessage(final String msg, final Exception e);
}