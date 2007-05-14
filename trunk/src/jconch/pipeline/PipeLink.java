package jconch.pipeline;

import static java.lang.Math.max;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.NullArgumentException;

/**
 * A link in the stages of the pipeline. This class takes responsibility for all
 * of the concurrency handling during hand-offs between pipelines.
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
			throw new IllegalArgumentException(
					"Fetch timeout must be nonnegative");
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
		try {
			return q.poll(max(1, getFetchTimeout()), TimeUnit.MILLISECONDS);
		} catch (InterruptedException ie) {
			return null;
		}
	}
}
