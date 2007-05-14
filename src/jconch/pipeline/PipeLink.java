package jconch.pipeline;

import static java.lang.Math.max;

import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.set.MapBackedSet;
import org.apache.commons.collections.set.SynchronizedSet;
import org.apache.commons.lang.NullArgumentException;

/**
 * <p>
 * A link in the stages of the pipeline. This class takes responsibility for all
 * of the concurrency handling during hand-offs between pipelines. Furthermore,
 * it abstracts the nature of the preceeding and succeeding elements -- as long
 * as something feeds into this class or reads out of this class, it can be
 * merged into a standard pipeline.
 * </p>
 * <p>
 * This class has a concept of a "fetch timeout" ({@link #getFetchTimeout()}/{@link #setFetchTimeout(long)}).
 * Although adding new elements will always be accepted, it is possible that a
 * stall might cause a delay in processing. The fetch timeout is the amount of
 * time to wait before the stall is detected. The default value is
 * <code>0</code>, which means to not wait at all, but act in pass-through
 * mode.
 * </p>
 * <p>
 * The link requires at least one <em>source</em> and one <em>sink</em> to
 * be registered before {@link #add(Object)} and {@link #get()} are usable. A
 * source will place elements into the link, and a sink will draw elements from
 * it. These registries are used for tracking status.
 * </p>
 * 
 * @author Robert Fischer
 */
public class PipeLink<T> {

    private final BlockingQueue<T> q;

    /**
     * The timeout on fetches.
     */
    private AtomicLong fetchTimeout = new AtomicLong(0L);

    /**
     * The sources of this link.
     */
    private final Set<Object> sources = SynchronizedSet.decorate(MapBackedSet.decorate(new WeakHashMap(2)));

    /**
     * The sinks of this link.
     */
    private final Set<Object> sinks = SynchronizedSet.decorate(MapBackedSet.decorate(new WeakHashMap(2)));

    /**
     * Constructor.
     * 
     * @param queue
     *            The queue to use.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    protected PipeLink(final BlockingQueue<T> queue) {
        if (queue == null) {
            throw new NullArgumentException("queue");
        }
        this.q = queue;
    }

    /**
     * Registers a source for this pipe link.
     * 
     * @param source
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    public void registerSource(final Object source) {
        if (source == null) {
            throw new NullArgumentException("source");
        }
        this.sources.add(source);
    }

    /**
     * Registers a sink for this pipe link.
     * 
     * @param sink
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    public void registerSink(final Object sink) {
        if (sink == null) {
            throw new NullArgumentException("sink");
        }
        this.sinks.add(sink);
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
        // TODO Check we have a sink
        final boolean out = q.offer(in);
        return out;
    }

    /**
     * Gets the timeout on fetch operations of the instance.
     * 
     * @return the fetch timeout.
     */
    public long getFetchTimeout() {
        return fetchTimeout.get();
    }

    /**
     * Sets the fetch timeout of the instance. Positive values represent
     * milliseconds to wait, and 0 means to not wait at all.
     * 
     * @param fetchTimeout
     *            the fetch timeout to set
     * @throws IllegalArgumentException
     *             If the argument is < 0.
     */
    public void setFetchTimeout(final long newFetchTimeout) {
        if (newFetchTimeout < 0) {
            throw new IllegalArgumentException("Fetch timeout must be nonnegative");
        }
        fetchTimeout.set(newFetchTimeout);
    }

    /**
     * Removes an element from the link, if any is available. This method may
     * block for up to {@link #getFetchTimeout()} milliseconds for a value to
     * become available.
     * 
     * @return The removed element, or <code>null</code> if the link is empty.
     */
    public T get() {
        // TODO If we don't have a source, don't bother waiting on the timeout
        try {
            return q.poll(max(1, getFetchTimeout()), TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
            return null;
        }
    }
}
