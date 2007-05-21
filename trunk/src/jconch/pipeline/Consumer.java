package jconch.pipeline;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.NullArgumentException;

/**
 * The base implementation of a pipe that consumes elements without producing
 * anything.
 * 
 * @param T
 *            The type that is produced.
 * @author Robert Fischer
 */
public abstract class Consumer<T> extends PipelineStage {

    /**
     * The inbound pipeline.
     */
    protected final PipeLink<T> link;

    /**
     * If we've seen a <code>null</code> value from the link.
     */
    private final AtomicBoolean sawNull = new AtomicBoolean(false);

    /**
     * Creates a new instance of <code>Consumer</code>.
     * 
     * @param threading
     *            The model for this consumer to use.
     * @param in
     *            The link into this stage.
     * @throws NullArgumentException
     *             If either argument is <code>null</code>
     */
    protected Consumer(final ThreadingModel threading, final PipeLink<T> in) {
        super(threading);
        if (threading == null) {
            throw new NullArgumentException("threading");
        }
        if (in == null) {
            throw new NullArgumentException("in");
        }
        link = in;
    }

    /**
     * Responsible for consuming items.
     * 
     * @param item
     *            The item to consume; never <code>null</code>
     */
    public abstract void consumeItem(final T item);

    /**
     * Fetches an object and calls {@link #consumeItem(Object)} on it.
     */
    @Override
    public final void execute() {
        // Make sure we're not already done
        final IllegalStateException ise;
        if (sawNull.get()) {
            ise = new IllegalStateException("Already exhausted incoming pipeline");
        } else if (isFinished()) {
            // TODO Check all the previous conditions once more
            // (Could be a race condition)
            ise = new IllegalStateException("Indeterminant reason");
        } else {
            ise = null;
        }
        if (ise != null) {
            logMessage("Called execute at wrong time", ise);
            return;
        }

        // Get the target
        final T target;
        try {
            target = link.get();
        } catch (Exception e) {
            logMessage("Unknown exception when retrieving object", e);
            return;
        }

        // Now delegate to the polymorphic consumption
        if (target == null) {
            sawNull.set(true);
        } else {
            try {
                consumeItem(target);
            } catch (Exception e) {
                logMessage("Unknown exception when consuming object", e);
            }
        }
    }

    /**
     * In addition to the super implementation, it checks for error conditions.
     * 
     * {@inheritDoc}
     */
    @Override
    public boolean isFinished() {
        return super.isFinished() || sawNull.get();
    }
}
