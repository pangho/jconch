package jconch.pipeline;

/**
 * A pipeline stage that simply captures the thread that executed it. Useful in
 * unit tests to catch threading models.
 * 
 * @author Robert Fischer
 */
class ThreadCapturingPipelineStage extends PipelineStage {

	public Thread executeThread = null;

	public ThreadCapturingPipelineStage(final ThreadingModel threading) {
		super(threading);
	}

	@Override
	void execute() {
		executeThread = Thread.currentThread();
	}

	@Override
	protected void logMessage(String msg, Exception e) {
		// Does nothing
	}

	@Override
	public boolean isFinished() {
		return executeThread != null;
	}

}
