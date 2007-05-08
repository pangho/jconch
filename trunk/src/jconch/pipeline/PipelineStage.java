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
	 */
	abstract void execute();

	/**
	 * Determines if the pipeline stage is exhausted.
	 * 
	 * @return If the stage is exhausted.
	 */
	abstract boolean isExhausted();
}
