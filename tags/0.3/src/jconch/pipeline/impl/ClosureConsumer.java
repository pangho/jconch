package jconch.pipeline.impl;

import jconch.pipeline.*;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.NullArgumentException;

/**
 * A {@link Closure}-based implementation of a {@link Consumer}.
 * 
 * @author Robert Fischer
 */
public abstract class ClosureConsumer<T> extends Consumer<T> {

    /**
     * The implementation of the consumption.
     */
    private final Closure eater;

    /**
     * Constructor.
     * 
     * @param sink
     *            The implementation of the consumption.
     * @param threading
     *            The model for this consumer to use.
     * @param in
     *            The link into this stage.
     * @throws NullArgumentException
     *             If any argument is <code>null</code>
     */
    public ClosureConsumer(final Closure sink, final ThreadingModel threading, final PipeLink<T> in) {
        super(threading, in);
        if (sink == null) {
            throw new NullArgumentException("sink");
        }
        eater = sink;
    }

    /**
     * Delegates implementation to {@link Closure#execute(Object)}.
     */
    @Override
    public void consumeItem(final Object item) {
        eater.execute(item);
    }

}
