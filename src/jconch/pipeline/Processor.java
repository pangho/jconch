package jconch.pipeline;

import java.util.*;

import jconch.pipeline.impl.*;

import org.apache.commons.collections.buffer.UnboundedFifoBuffer;

/**
 * <p>
 * A processing pipe in the pipeline.
 * </p>
 * 
 * @param <IN_T>
 *            The input type.
 * @param <OUT_T>
 *            The output type.
 * @author Robert Fischer
 */
public abstract class Processor<IN_T, OUT_T> extends PipelineStage {

    private final CollectionConsumer<IN_T> in;

    private final CollectionProducer<OUT_T> out;

    /**
     * Creates a new intance of <code>Processor</code>.
     * 
     * @param threading
     */
    @SuppressWarnings("unchecked")
    protected Processor(final ThreadingModel threading, final PipeLink<IN_T> inLink, final PipeLink<OUT_T> outLink) {
        super(threading);
        final Processor<IN_T, OUT_T> me = this;
        in = new CollectionConsumer<IN_T>(new ExceptionThreadingModel(), inLink) {
            @Override
            public void logMessage(final String msg, final Exception e) {
                me.logMessage("Inbound Error: " + msg, e);
            }
        };
        out = new CollectionProducer<OUT_T>(new UnboundedFifoBuffer(), new ExceptionThreadingModel(), outLink) {
            @Override
            public void logMessage(final String msg, final Exception e) {
                me.logMessage("Outbound Error: " + msg, e);
            }
        };
    }

    /**
     * The argument that implements the processing for this class.
     * 
     * @param item
     *            The item to process.
     * @return The processed item, or <code>null</code> to drop it.
     */
    public abstract OUT_T process(final IN_T item);

    @Override
    public final void execute() {
        // First, draw an element in
        in.execute();

        // See if we got something
        final IN_T obj;
        try {
            final Collection<IN_T> c = in.getCollection();
            synchronized (c) {
                final Iterator<IN_T> it = c.iterator();
                obj = it.next();
                it.remove();
            }
        } catch (final NoSuchElementException nsee) {
            // Already logged the failure to read something in
            return;
        }
        if (obj == null) {
            return;
        }
        final OUT_T item = process(obj);
        if (item == null) {
            // This means to drop an item.
            return;
        }

        // Now put the element out
        out.getCollection().add(item);
        out.execute();
    }

    @Override
    public boolean isFinished() {
        return (in.isFinished() && out.isFinished());
    }

    /**
     * Provides the pipeline link out.
     * 
     * @return The link that the producer feeds into; never <code>null</code>.
     */
    public PipeLink<OUT_T> getLinkOut() {
        return this.out.getLinkOut();
    }

    /**
     * Provides the pipe link that is being drawn from.
     * 
     * @return The inbound pipe link.
     */
    protected PipeLink<IN_T> getLinkIn() {
        return this.in.getLinkIn();
    }

}
