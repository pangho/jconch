package jconch.pipeline.impl;

import jconch.pipeline.PipeLink;
import jconch.pipeline.Producer;
import jconch.pipeline.ThreadingModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.NullArgumentException;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A producer which provides elements from a given collection.
 *
 * @author Robert Fischer
 */
public abstract class CollectionProducer<T> extends Producer<T> {

    private final Collection<T> elts;

    /**
     * Constructor.
     *
     * @param data The collection to draw elements from.
     * @throws NullArgumentException If the argument is <code>null</code>
     */
    public CollectionProducer(final Collection<T> data,
                              final ThreadingModel model, final PipeLink<T> out) {
        super(model, out);
        if (data == null) {
            throw new NullArgumentException("data");
        }
        elts = CollectionUtils.synchronizedCollection(data);
    }

    /**
     * Determines if the producer is done producing elements.
     *
     * @return If the collection is empty.
     */
    @Override
    protected boolean isExhausted() {
        synchronized (elts) {
            return elts.isEmpty();
        }
    }

    /**
     * @return
     */
    @Override
    public T produceItem() {
        try {
            final T out;
            synchronized (elts) {
                final Iterator<T> it = elts.iterator();
                out = it.next();
                it.remove();
            }
            return out;
        } catch (NoSuchElementException nsee) {
            return null;
        }
    }

    /**
     * Gets the collection backing the instance.
     */
    public Collection<T> getCollection() {
        return elts;
    }

}
