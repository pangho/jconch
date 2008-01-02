package jconch.pipeline.impl;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

import jconch.pipeline.PipeStage;
import jconch.pipeline.ThreadingModel;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.time.DateUtils;

/**
 * <p>
 * A wrapper around {@link ThreadingModel} which allows the user to leverage the
 * {@link Executor} API, and its myriad extensions. This class launches a
 * seperate thread to monitor the pipeline stage it is wrapping and populate the
 * executor, which means there can be multiple instances of this class in a
 * pipeline which execute asynchronously.
 * </p>
 * <p>
 * This class is thread safe: the state may be modified safely during execution.
 * </p>
 * <p>
 * This class is somewhat optimized for a common case: {@link #finalize()} will
 * detect if a {@link ThreadPoolExecutor} is being used and will call
 * {@link ThreadPoolExecutor#shutdown()} to enable an orderly shutdown.
 * </p>
 * 
 * <p>
 * <a href="ExecutorThreadingModel.java.html">View Source</a>
 * </p>
 * 
 * @author Robert Fischer
 */
public class ExecutorThreadingModel implements ThreadingModel {

    private final Executor exec;

    private AtomicLong spawnDelay = new AtomicLong(DateUtils.MILLIS_PER_SECOND);

    /**
     * Constructor. This uses the given implementation, and a default delay
     * between spawns.
     * 
     * @param impl
     *            The underlying implementation for execution.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>.
     */
    public ExecutorThreadingModel(final Executor impl) {
        if (impl == null) {
            throw new NullArgumentException("impl");
        }
        this.exec = impl;
    }

    /**
     * Delegates the execution of the argument to the underlying executor. Each
     * call to {@link PipeStage#execute()} is wrapped in its own
     * {@link Runnable} that is passed to the {@link Executor} implementation
     * provided.
     * 
     * @param toRun
     *            The stage to execute.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>.
     */
    public void execute(final PipeStage toRun) {
        if (toRun == null) {
            throw new NullArgumentException("toRun");
        }

        // Launch the populator thread
        new Thread() {
            @Override
            public void run() {
                // This is the big loop
                while (!toRun.isFinished()) {
                    // Set the executor to execute toRun once
                    exec.execute(new Runnable() {
                        public void run() {
                            if (!toRun.isFinished()) {
                                toRun.execute();
                            }
                        }
                    });

                    // Now wait the established period
                    try {
                        Thread.sleep(spawnDelay.get());
                    } catch (InterruptedException e) {
                        Thread.yield();
                    }
                }
            }
        }.start();
    }

    /**
     * Gets the period between executor calls for instance.
     * 
     * @return the spawn period
     */
    public long getSpawnPeriod() {
        return spawnDelay.get();
    }

    /**
     * Sets the period between executor calls for instance. Values less than or
     * equal to 0 are treated as 1.
     * 
     * @param spawnDelay
     *            the new spawn period
     */
    public void setSpawnDelay(final long spawnDelay) {
        this.spawnDelay.set(Math.max(spawnDelay, 1));
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            if (exec instanceof ThreadPoolExecutor) {
                ((ThreadPoolExecutor) exec).shutdown();
            }
        } finally {
            super.finalize();
        }
    }

}
