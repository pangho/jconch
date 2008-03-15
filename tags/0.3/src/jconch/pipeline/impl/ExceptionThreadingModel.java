package jconch.pipeline.impl;

import jconch.pipeline.PipeStage;
import jconch.pipeline.ThreadingModel;

/**
 * Class that explodes when {@link #execute(PipeStage)} is called.
 */
public class ExceptionThreadingModel implements ThreadingModel {

    /**
     * @throws RuntimeException
     *             Always.
     */
    public void execute(final PipeStage toRun) {
        throw new RuntimeException();
    }

}
