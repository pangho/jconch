package jconch.pipeline.impl;

import jconch.pipeline.PipeStage;
import jconch.pipeline.ThreadingModel;

import org.apache.commons.lang.NullArgumentException;

/**
 * Executes the pipeline stage inline. Does not actually spawn a thread, but
 * simply delegates the execution again.
 * 
 * @author Robert Fischer
 * 
 */
public class InlineThreadingModel implements ThreadingModel {

	/**
	 * Simply calls {@link PipeStage#execute()}.
	 * 
	 * @throws NullArgumentException
	 *             If the argument is <code>null</code>
	 */
	public void execute(final PipeStage toRun) {
		if (toRun == null) {
			throw new NullArgumentException("toRun");
		}
		while (!toRun.isFinished()) {
			toRun.execute();
		}
	}
}
