package jconch.pipeline.builder;

import jconch.pipeline.Consumer;
import jconch.pipeline.PipeLink;
import jconch.pipeline.PipelineStage;
import jconch.pipeline.Processor;
import jconch.pipeline.Producer;
import jconch.pipeline.ThreadingModel;
import jconch.pipeline.impl.UnboundedPipeLink;

import org.apache.commons.lang.NullArgumentException;

/**
 * Class for programmatically building up a pipeline. It provides convenient
 * access to the {@link ThreadingModel} and {@link PipeLink} of the elements
 * {@link PipelineStage} that makes up the tail of the pipeline, as well as
 * enforcing type safety through generics. In addition, it starts the elements
 * (see {@link PipelineStage#start()}) as they are attached to the pipeline.
 * 
 * @author Robert Fischer
 */
public class PipelineBuilder<CURRENT_T> {

    private final PipeLink<CURRENT_T> link;

    private final ThreadingModel threads;

    /**
     * Constructor. Begins the process of building up the pipeline.
     * 
     * @param start
     *            The producer which starts the pipeline.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>.
     */
    public PipelineBuilder(final Producer<CURRENT_T> start) {
        if (start == null) {
            throw new NullArgumentException("start");
        }
        start.start();
        this.link = start.getLinkOut();
        this.threads = start.getThreadingModel();
    }

    /**
     * Private constructor for building next elements.
     * 
     * @param link
     *            The link to use.
     * @param threads
     *            The threading model to use.
     */
    private PipelineBuilder(final PipeLink<CURRENT_T> link, final ThreadingModel threads) {
        this.link = link;
        this.threads = threads;
    }

    /**
     * Attaches a processor onto the end of the pipeline.
     * 
     * @param <NEW_T>
     *            The new type that is being processed.
     * @param processor
     *            The processor which will process elements.
     * @return The builder for the next stage in the pipeline.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>.
     */
    public <NEW_T> PipelineBuilder<NEW_T> attachProcessor(final Processor<CURRENT_T, NEW_T> processor) {
        if (processor == null) {
            throw new NullArgumentException("processor");
        }
        processor.start();
        return new PipelineBuilder<NEW_T>(processor.getLinkOut(), this.threads);
    }

    /**
     * Attaches a consumer onto the end of the pipeline.
     * 
     * @param consumer
     *            The consumer to attach.
     */
    public void attachConsumer(final Consumer<CURRENT_T> consumer) {
        if (consumer == null) {
            throw new NullArgumentException("consumer");
        }
        consumer.start();
        return;
    }

    /**
     * Gets the link of the instance.
     * 
     * @return the link.
     */
    public PipeLink<CURRENT_T> getLink() {
        return link;
    }

    /**
     * Gets the threading model of the element which was last attached.
     * 
     * @return the threading model.
     */
    public ThreadingModel getThreadingModel() {
        return threads;
    }
}