package jconch.pipeline;

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
    protected Processor(final ThreadingModel threading, final PipeLink<IN_T> inLink, final PipeLink<OUT_T> outLink) {
        super(threading);
        final Processor me = this;
        in = new CollectionConsumer<IN_T>(new UnboundedFifoBuffer(), new ExceptionThreadingModel(), inLink) {
            @Override
            protected void logMessage(String msg, Exception e) {
                me.logMessage(msg, e);
            }
        };
        out = new CollectionProducer<OUT_T>(new UnboundedFifoBuffer(), new ExceptionThreadingModel(), outLink) {
            @Override
            protected void logMessage(String msg, Exception e) {
                me.logMessage(msg, e);
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
    final void execute() {
        // First, draw an element in
        in.execute();

        // See if we got something
        final IN_T obj = in.getCollection().iterator().next();
        if (obj == null) {
            return;
        }
        final OUT_T item = process(obj);
        if (item == null) {
            return;
        }

        // Now put the element out
        out.getCollection().add(item);
        out.execute();
    }

    @Override
    public boolean isFinished() {
        return super.isFinished() || (in.isFinished() && out.isFinished());
    }

}
