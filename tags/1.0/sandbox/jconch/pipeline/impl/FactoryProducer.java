package jconch.pipeline.impl;

import jconch.pipeline.PipeLink;
import jconch.pipeline.Producer;
import jconch.pipeline.ThreadingModel;
import org.apache.commons.collections.Factory;
import org.apache.commons.lang.NullArgumentException;

/**
 * A {@link Producer} based off of a {@link Factory} implementation.
 * <p/>
 * <b>Implementation Note:</b> Due to <a
 * href="http://enfranchisedmind.com/blog/archive/2007/05/17/232" target="v">a
 * failing of generics</a>, there is no type-safety available for this
 * implementation.
 *
 * @author Robert Fischer
 */
public abstract class FactoryProducer extends Producer {

    /**
     * The factory that provides elements.
     */
    private final Factory maker;

    /**
     * The next element that will be returned.
     */
    private Object nextElement;

    /**
     * Constructor.
     *
     * @param source    The factory that will be called to produce new elements.
     * @param threading The threading model.
     * @param link      The link we produce things into.
     * @throws NullArgumentException If any argument is <code>null</code>.
     */
    protected FactoryProducer(final Factory source, final ThreadingModel threading, final PipeLink link) {
        super(threading, link);
        if (source == null) {
            throw new NullArgumentException("source");
        }
        this.maker = source;
        assignNextElement();
    }

    /**
     * Assigns the next element.
     */
    private final void assignNextElement() {
        try {
            this.nextElement = maker.create();
        } catch (Exception e) {
            logMessage("Cannot assign the next element; terminating production", e);
            this.nextElement = null;
        }
    }

    @Override
    protected synchronized boolean isExhausted() {
        return this.nextElement == null;
    }

    @Override
    public synchronized Object produceItem() {
        final Object out = this.nextElement;
        assignNextElement();
        return out;
    }

}
