package jconch.pipeline;

/**
 * Class that explodes when {@link #execute(PipelineStage)} is called.
 */
class ExceptionThreadingModel implements ThreadingModel {

    /**
     * @throws RuntimeException
     *             Always.
     */
    public void execute(final PipelineStage toRun) {
        throw new RuntimeException();
    }

}
