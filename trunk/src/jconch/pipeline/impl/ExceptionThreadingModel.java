package jconch.pipeline.impl;

import jconch.pipeline.PipelineStage;
import jconch.pipeline.ThreadingModel;

/**
 * Class that explodes when {@link #execute(PipelineStage)} is called.
 */
public class ExceptionThreadingModel implements ThreadingModel {

    /**
     * @throws RuntimeException
     *             Always.
     */
    public void execute(final PipelineStage toRun) {
        throw new RuntimeException();
    }

}
