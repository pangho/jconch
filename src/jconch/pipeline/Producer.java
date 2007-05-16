package jconch.pipeline;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.UnhandledException;

/**
 * The base implementation of a pipe that produces new elements to retrieve.
 * 
 * @param <OUT_T>
 *            The type of the object that is produced by this element.
 * @author Robert Fischer
 */
public abstract class Producer<OUT_T> extends PipelineStage {

    /**
     * The link we drop into.
     */
    protected final PipeLink<OUT_T> link;

    /**
     * If we've created <code>null</code> before.
     */
    private final AtomicBoolean createdNull = new AtomicBoolean(false);

    /**
     * If we have failed to do an add before.
     */
    private final AtomicBoolean failedAdd = new AtomicBoolean(false);

    /**
     * Constructor.
     * 
     * @param threading
     *            The threading model.
     * @param link
     *            The link we produce things into.
     * @throws NullArgumentException
     *             If either argument is <code>null</code>.
     */
    protected Producer(final ThreadingModel threading, final PipeLink<OUT_T> link) {
        super(threading);
        if (link == null) {
            throw new NullArgumentException("link");
        }
        this.link = link;
        this.link.registerSource(this);
    }

    /**
     * Provides the pipeline link out.
     * 
     * @return The link that the producer feeds into; never <code>null</code>.
     */
    public PipeLink<OUT_T> getLinkOut() {
        return link;
    }

    /**
     * Method that must be implemented to queue the producer.
     * 
     * @return The next item for the producer, or <code>null</code> if there
     *         are no more elements.
     */
    public abstract OUT_T produceItem();

    /**
     * In addition to the basic checks, checks for error conditions.
     * 
     * {@inheritDoc}
     */
    @Override
    public final boolean isFinished() {
        return super.isFinished() || isExhausted() || createdNull.get() || failedAdd.get();
    }

    /**
     * Checks state, generates an element, and then puts it into the queue.
     */
    @Override
    final void execute() {
        // Make sure we're not already exhausted
        final IllegalStateException ise;
        if (createdNull.get()) {
            ise = new IllegalStateException("Previously created null element");
        } else if (failedAdd.get()) {
            ise = new IllegalStateException("Previously failed an add");
        } else if (isExhausted()) {
            ise = new IllegalStateException("Pipeline is exhausted");
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

        // Produce an element
        final OUT_T out;
        try {
            out = produceItem();
        } catch (final Exception e) {
            // We don't track this error condition, because that's what
            // #isExhausted is for!
            logMessage("Exception when attempting to produce an item", e);
            return;
        }

        // Check to see we've got something
        if (out == null) {
            createdNull.set(true);
            logMessage("Null element retrieved", new NoSuchElementException("Cannot generate more elements"));
            return;
        }

        try {
            failedAdd.set(!this.link.add(out));
        } catch (Exception e) {
            failedAdd.set(true);
            logMessage("Exception when attempting to write item", e);
        }
    }

    /**
     * Determines if the pipeline stage will not produce any more elements.
     * 
     * @return If the stage is exhausted.
     */
    protected abstract boolean isExhausted();
}
