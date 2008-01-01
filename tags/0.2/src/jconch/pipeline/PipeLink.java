package jconch.pipeline;

import static java.lang.Math.*;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.collections.set.MapBackedSet;
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

    final BlockingQueue<T> q;

    /**
     * The timeout on fetches.
     */
    private final AtomicLong fetchTimeout = new AtomicLong(0L);

    /**
     * The timeout on puts.
     */
    private final AtomicLong putTimeout = new AtomicLong(0L);

    /**
     * The sources of this link.
     */
    @SuppressWarnings("unchecked")
    final Set<Producer<T>> sources = Collections.synchronizedSet(MapBackedSet.decorate(new WeakHashMap(2)));

    /**
     * Has this link been broken?
     */
    private final AtomicBoolean isBroken = new AtomicBoolean(false);

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
    public void registerSource(final Producer<T> source) {
        if (source == null) {
            throw new NullArgumentException("source");
        }
        this.sources.add(source);
    }

    /**
     * Filters out the finished sources.
     */
    private final void checkSources() {
        // This is really friggin' tricky because of threading issues and the
        // weak references aspect.
        // Best to work off our own (hard referenced) copy of the set elements,
        // and make atomic calls back to sources.
        final Producer[] sourcesArr = sources.toArray(new Producer[sources.size()]);
        for (int i = 0; i < sourcesArr.length; i++) {
            final Producer source = sourcesArr[i];
            if ((source != null) && source.isFinished()) {
                sources.remove(source);
            }
            sourcesArr[i] = null; // Allow it to be GCed
        }
    }

    /**
     * Adds an element into the link, if at all possible.
     * 
     * @param in
     *            The element to add.
     * @return <code>true</code> if the add succeeded
     * @throws NullArgumentException
     *             If the argument is <code>null</code>
     */
    public boolean add(final T in) {
        if (in == null) {
            throw new NullArgumentException("in");
        }
        if (isBroken.get()) {
            return false;
        }
        try {
            return q.offer(in, max(1, putTimeout.get()), TimeUnit.MILLISECONDS);
        } catch (final InterruptedException e) {
            return false;
        }
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
            throw new IllegalArgumentException("Fetch timeout must be nonnegative; was " + newFetchTimeout);
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
        // Check for the broken short-circuit
        if (isBroken.get()) {
            return null;
        }

        // See if we get anything off the top
        final T attempt1 = q.poll();
        if (attempt1 != null) {
            return attempt1;
        }

        // Now drop into the loop
        try {
            final long fetchTime = max(1, getFetchTimeout());
            final long endTime = System.currentTimeMillis() + fetchTime;
            final long iterTime = max(1, fetchTime / 10);
            do {
                // See if we're expecting something to come
                if (sources.isEmpty()) {
                    // No sources: just return whatever we've got (if anything)
                    return q.poll();
                } else {
                    // Sit on the wait for a while
                    final T out = q.poll(iterTime, TimeUnit.MILLISECONDS);
                    if (out != null) {
                        return out;
                    } else {
                        // Waited, didn't get anything -- drat.
                        // Double-check our sources.
                        checkSources();
                    }
                }
            } while (System.currentTimeMillis() <= endTime);

            // Didn't see anything in our time period
            return null;
        } catch (final InterruptedException ie) {
            return null;
        }
    }

    /**
     * Gets the timeout on add operations of the instance.
     * 
     * @return the add timeout.
     */
    public long getAddTimeout() {
        return putTimeout.get();
    }

    /**
     * Sets the add timeout of the instance. Positive values represent
     * milliseconds to wait, and 0 means to not wait at all.
     * 
     * @param fetchTimeout
     *            the add timeout to set
     * @throws IllegalArgumentException
     *             If the argument is < 0.
     */
    public void setAddTimeout(final long timeout) {
        if (timeout < 0) {
            throw new IllegalArgumentException("Timeout must be positive; was " + timeout);
        }
        this.putTimeout.set(timeout);
    }

    /**
     * Provides the number of elements currently in the queue.
     * 
     * @return The minimum of the number of queued elements and
     *         {@link Integer#MAX_VALUE}
     */
    public int getQueueLength() {
        return this.q.size();
    }

    /**
     * Provides an estimate of the number of elements the queue could
     * additionally hold.
     * 
     * @return An estimate of the capcity of the queue, or
     *         {@link Integer#MAX_VALUE} if it is unbounded.
     */
    public int getRemainingCapacity() {
        return this.q.remainingCapacity();
    }

    /**
     * Drops all elements from this link.
     */
    public void clearQueue() {
        this.q.clear();
    }

    /**
     * Breaks the link completely and permanently. Any new {@link #add(Object)}
     * or {@link #get()} calls will fail after calling this method.
     */
    public void breakLink() {
        isBroken.set(true);
    }
}
