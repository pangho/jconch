package jconch.pipeline;

import java.util.NoSuchElementException;

import org.apache.commons.lang.NullArgumentException;

/**
 * The interface denoting that this element in the pipe produces new elements to
 * retrieve.
 * 
 * @param <OUT_T>
 *            The type of the object that is produced by this element.
 * @author Robert Fischer
 */
public abstract class Producer<OUT_T> extends PipelineStage {

	/**
	 * The link we drop into.
	 */
	protected final PipeLink<OUT_T, ?> link;

	/**
	 * Constructor.
	 * 
	 * @param threading
	 *            The threading model.
	 * @param link
	 *            The link we produce things into.
	 * @throws NullArgumentException
	 *             If either argument is <code>null</code>.
	 */
	protected Producer(final ThreadingModel threading,
			final PipeLink<OUT_T, ?> link) {
		super(threading);
		if (link == null) {
			throw new NullArgumentException("link");
		}
		this.link = link;
	}

	/**
	 * Provides the pipeline link out.
	 * 
	 * @return The link that the producer feeds into; never <code>null</code>.
	 */
	public PipeLink<OUT_T, ?> getLinkOut() {
		return link;
	}

	/**
	 * Method that must be implemented to queue the producer.
	 * 
	 * @return The next item for the producer.
	 * @throws NoSuchElementException
	 *             If there are no more elements.
	 */
	public abstract OUT_T produceItem();

	/**
	 * If more elements will be produced. If the current state is indeterminant,
	 * this must block until it has an answer.
	 * 
	 * @return If there are more elements.
	 */
	public abstract boolean isExhausted();
}
