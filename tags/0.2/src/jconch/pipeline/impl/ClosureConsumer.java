package jconch.pipeline.impl;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.NullArgumentException;

import jconch.pipeline.Consumer;
import jconch.pipeline.PipeLink;
import jconch.pipeline.ThreadingModel;

/**
 * A {@link Closure}-based implementation of a {@link Consumer}.
 * 
 * <b>Implementation Note:</b> Due to <a
 * href="http://enfranchisedmind.com/blog/archive/2007/05/17/232" target="v">a
 * failing of generics</a>, there is no type-safety available for this
 * implementation.
 * 
 * @author Robert Fischer
 */
public abstract class ClosureConsumer extends Consumer {

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
    protected ClosureConsumer(final Closure sink, ThreadingModel threading, PipeLink in) {
        super(threading, in);
        if (sink == null) {
            throw new NullArgumentException("sink");
        }
        this.eater = sink;
    }

    /**
     * Delegates implementation to {@link Closure#execute(Object)}.
     */
    @Override
    public void consumeItem(final Object item) {
        eater.execute(item);
    }

}
