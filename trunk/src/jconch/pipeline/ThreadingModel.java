package jconch.pipeline;

import org.apache.commons.lang.NullArgumentException;

/**
 * Implements a threading model for a pipeline. A threading model abstracts out
 * the way in which pipelines are processed.
 * 
 * @author Robert Fischer
 */
public interface ThreadingModel {

	/**
	 * Wraps the execution of the pipeline in the appropriate threading.
	 * 
	 * @param toRun
	 *            The stage to run.
	 * @throws NullArgumentException
	 *             If the argument is <code>null</code>
	 */
	void execute(final PipelineStage toRun);
}
