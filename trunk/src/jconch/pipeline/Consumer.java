package jconch.pipeline;

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

    protected final PipeLink<T> link;

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
     * 
     */
    @Override
    final void execute() {
        // TODO Code this up.
    }

    /**
     * In addition to the super implementation, it checks for error conditions.
     * 
     * {@inheritDoc}
     */
    @Override
    public final boolean isFinished() {
        return super.isFinished();
    }
}
