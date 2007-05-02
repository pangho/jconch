package jconch.pipeline;

import java.util.NoSuchElementException;

/**
 * The interface denoting that this element in the pipe produces new elements to
 * retrieve.
 * 
 * @param <OUT_T>
 *            The type of the object that is produced by this element.
 * @author Robert Fischer
 */
public interface Producer<OUT_T> extends PipelineStage {

    /**
     * Provides the pipeline link out.
     * 
     * @return The link that the producer feeds into; never <code>null</code>.
     */
    PipeLink getLinkOut();
}
