package jconch.pipeline;

/**
 * An element in a pipeline.
 * 
 * @author Robert
 */
public interface PipeElement {

    /**
     * <p>
     * Signals to the element that it should now be fully ready to handle items.
     * If there is any reason why the element cannot process items, it should
     * throw an exception when this is called.
     * </p>
     * <p>
     * This method should be safe to be called multiple times, possibly from
     * different threads.
     * </p>
     */
    public void start();
}
