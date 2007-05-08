package jconch.pipeline;

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
	 * Simply calls {@link PipelineStage#execute()}.
	 * 
	 * @throws NullArgumentException
	 *             If the argument is <code>null</code>
	 */
	public void execute(final PipelineStage toRun) {
		if (toRun == null) {
			throw new NullArgumentException("toRun");
		}
		while (!toRun.isExhausted()) {
			toRun.execute();
		}
	}
}
