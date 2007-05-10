package jconch.pipeline;

import java.util.Queue;

import org.apache.commons.lang.NullArgumentException;

/**
 * A link in the stages of the pipeline. This class takes responsibility for all
 * of the concurrency handling during hand-offs between pipelines.
 * 
 * @author Robert Fischer
 */
public class PipeLink<T> {

    private final Queue<T> q;

    /**
     * A token that is signaled whenever a get/add happens.
     */
    protected final Object boundary = new Object();

    /**
     * Constructor.
     * 
     * @param queue
     *            The queue to use.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    protected PipeLink(final Queue<T> queue) {
        if (queue == null) {
            throw new NullArgumentException("queue");
        }
        this.q = queue;
    }

    /**
     * Adds an element into the link, if at all possible.
     * 
     * @param in
     *            The element to add.
     * @returns <code>true</code> if the add succeeded
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    public boolean add(final T in) {
        if (in == null) {
            throw new NullArgumentException("in");
        }
        final boolean out = q.offer(in);
        boundary.notifyAll();
        return out;
    }

    /**
     * Removes an element from the link, if any is available.
     * 
     * @return The removed element, or <code>null</code> if the link is empty.
     */
    public T get() {
        final T out = q.poll();
        boundary.notifyAll();
        return out;
    }
}