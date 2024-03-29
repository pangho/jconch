package jconch.pipeline.impl;

import jconch.pipeline.PipeStage;
import jconch.pipeline.ThreadingModel;
import org.apache.commons.lang.NullArgumentException;

/**
 * Runs a single different thread to process the threading.
 *
 * @author Robert Fischer
 */
public class SingleThreadThreadingModel implements ThreadingModel {

    /**
     * Spawns a thread, and it keeps executing everything.
     *
     * @throws NullArgumentException If the argument is <code>null</code>
     */
    public void execute(final PipeStage toRun) {
        if (toRun == null) {
            throw new NullArgumentException("toRun");
        }
        new Thread() {
            @Override
            public void run() {
                while (!toRun.isFinished()) {
                    try {
                        toRun.execute();
                    } catch (Exception e) {
                        toRun.logMessage("Unhandled error during execute", e);
                    }
                }
            }
        }.start();
    }

}
