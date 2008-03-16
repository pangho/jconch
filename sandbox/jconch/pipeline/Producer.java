package jconch.pipeline;

import static org.apache.commons.collections.SetUtils.synchronizedSet;
import static org.apache.commons.collections.SetUtils.typedSet;
import org.apache.commons.lang.NullArgumentException;

import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * The base implementation of a pipe that produces new elements to retrieve.
 *
 * @author Robert Fischer
 * @param <OUT_T>
 * The type of the object that is produced by this element.
 */
public abstract class Producer<OUT_T> extends PipeStage {

    /**
     * The kinds of errors that we can have.
     */
    private enum Errs {
        failedAdd("Previously failed an add"), alreadyExhausted("This producer is exhausted"), errorsInProduction(
            "There was an exception when we produced a previous item"), createdNull(
            "Previously created null element");

        final String message;

        private Errs(final String msg) {
            this.message = msg;
        }
    }

    /**
     * The link we drop into.
     */
    protected final PipeLink<OUT_T> link;

    /**
     * The current set of errors. Once an item is added to a set, you CANNOT
     * delete from this set.
     */
    @SuppressWarnings("unchecked")
    private final Set<Errs> errors = typedSet(synchronizedSet(EnumSet.noneOf(Errs.class)), Errs.class);

    /**
     * Constructor.
     *
     * @param threading The threading model.
     * @param link      The link we produce things into.
     * @throws NullArgumentException If either argument is <code>null</code>.
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
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public final boolean isFinished() {
        if (!errors.isEmpty()) {
            return true;
        } else if (isExhausted()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks state, generates an element, and then puts it into the queue.
     */
    @Override
    public final void execute() {
        // Make sure we're not already exhausted
        final IllegalStateException ise;
        if (!errors.isEmpty()) {
            ise = new IllegalStateException(errors.iterator().next().message);
        } else if (isFinished()) {
            ise = new IllegalStateException("Indeterminant reason");
        } else if (!errors.isEmpty()) {
            // Double-check is to handle race condition possibility
            ise = new IllegalStateException(errors.iterator().next().message);
        } else {
            ise = null;
        }
        if (ise != null) {
            logMessage("Called execute at wrong time", ise);
            throw ise;
        }

        // Produce an element
        final OUT_T out;
        try {
            out = produceItem();
        } catch (final Exception e) {
            errors.add(Errs.errorsInProduction);
            logMessage("Exception when attempting to produce an item", e);
            return;
        }

        // Check to see we've got something
        if (out == null) {
            errors.add(Errs.createdNull);
            logMessage("Null element retrieved", new NoSuchElementException("Beyond the end of the pipe"));
            return;
        }

        try {
            if (!link.add(out)) {
                throw new RuntimeException("Adding to pipe link failed");
            }
        } catch (final Exception e) {
            errors.add(Errs.failedAdd);
            logMessage("Exception when attempting to write item", e);
            return;
        }
    }

    /**
     * Determines if the pipeline stage will not produce any more elements. This
     * may be called more than once, and once it returns true, it shall always
     * return true.
     *
     * @return If the stage is exhausted.
     */
    protected abstract boolean isExhausted();

}
