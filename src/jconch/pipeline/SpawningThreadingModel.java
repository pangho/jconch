package jconch.pipeline;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.time.DateUtils;

/**
 * Spawns a new thread for each call to execute.
 * 
 * @author Robert Fischer
 * 
 */
public class SpawningThreadingModel implements ThreadingModel {

    private final long waitTime;

    /**
     * Constructor that uses a default wait time between spawns.
     */
    public SpawningThreadingModel() {
        this.waitTime = 1 * DateUtils.MILLIS_PER_SECOND;
    }

    /**
     * Constructor.
     * 
     * @param waitBetweenSpawns
     *            The time to wait between spawns.
     * @throws IllegalArgumentException
     *             If the argument <= 0
     */
    public SpawningThreadingModel(final long waitBetweenSpawns) {
        if (waitBetweenSpawns <= 0) {
            throw new IllegalArgumentException("Argument cannot be 0 or less");
        }
        this.waitTime = waitBetweenSpawns;
    }

    /**
     * Spawns a thread that calls {@link PipelineStage#execute()}.
     * 
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    public void execute(final PipelineStage toRun) {
        if (toRun == null) {
            throw new NullArgumentException("toRun");
        }
        while (!toRun.isFinished()) {
            final Thread t = new Thread() {
                @Override
                public void run() {
                    toRun.execute();
                }
            };
            t.start();
            try {
                t.join(waitTime);
            } catch (InterruptedException e) {
                Thread.yield();
            }
        }
    }
}
