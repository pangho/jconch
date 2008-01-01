package jconch.pipeline.impl;

import java.util.Collection;

import jconch.pipeline.Consumer;
import jconch.pipeline.PipeLink;
import jconch.pipeline.ThreadingModel;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.buffer.UnboundedFifoBuffer;
import org.apache.commons.lang.NullArgumentException;

/**
 * A consumer which collects elements into a collection.
 * 
 * @author Robert Fischer
 */
public abstract class CollectionConsumer<T> extends Consumer<T> {

	private final Collection<T> c;

	public CollectionConsumer(final ThreadingModel threading,
			final PipeLink<T> in) {
		this(new UnboundedFifoBuffer(), threading, in);
	}

	/**
	 * Constructor.
	 * 
	 * @param out
	 *            The collection to be populated.
	 * @param threading
	 *            The threading model to use.
	 * @param in
	 *            The pipeline link to draw from.
	 * @throws NullArgumentException
	 *             If any argument is <code>null</code>
	 */
	public CollectionConsumer(final Collection<T> out,
			final ThreadingModel threading, final PipeLink<T> in) {
		super(threading, in);
		if (out == null) {
			throw new NullArgumentException("out");
		}
		this.c = CollectionUtils.synchronizedCollection(out);
	}

	@Override
	public void consumeItem(T item) {
		if (item != null) {
			this.c.add(item);
		}
	}

	/**
	 * Gets the collection backing the instance.
	 */
	public Collection<T> getCollection() {
		return c;
	}

}
