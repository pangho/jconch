package jconch.pipeline.impl;

import java.util.concurrent.atomic.AtomicLong;

import jconch.pipeline.PipeStage;
import jconch.pipeline.ThreadingModel;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.time.DateUtils;

/**
 * Spawns a new thread for each call to execute.
 * 
 * @author Robert Fischer
 * 
 */
public class SpawningThreadingModel implements ThreadingModel {

    private final AtomicLong waitTime;

    /**
     * Constructor that uses a default wait time between spawns.
     */
    public SpawningThreadingModel() {
        this(1 * DateUtils.MILLIS_PER_SECOND);
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
        this.waitTime = new AtomicLong(waitBetweenSpawns);
    }

    /**
     * Spawns a thread that calls {@link PipeStage#execute()}.
     * 
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    public void execute(final PipeStage toRun) {
        if (toRun == null) {
            throw new NullArgumentException("toRun");
        }
        while (!toRun.isFinished()) {
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        toRun.execute();
                    } catch (Exception e) {
                        toRun.logMessage("Unhandled error during execute", e);
                    }
                }
            };
            t.start();
            try {
                t.join(waitTime.get());
            } catch (InterruptedException e) {
                Thread.yield();
            }
        }
    }

    public long getSpawnPeriod() {
        return waitTime.get();
    }

    public void setSpawnPeriod(final long period) {
        if (period <= 0) {
            throw new IllegalArgumentException("Argument cannot be 0 or less");
        }
        this.waitTime.set(period);
    }
}
