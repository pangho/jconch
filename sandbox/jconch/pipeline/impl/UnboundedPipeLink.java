package jconch.pipeline.impl;

import jconch.pipeline.PipeLink;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * <p>
 * An unbounded link in the pipeline. This is a very fast implementation of a
 * link, but allows potentially unlimited elements to build up, which can be
 * counter-productive to the multithreaded approach.
 * </p>
 * <p/>
 * <p>
 * <a href="UnboundedPipeLink.java.html">View Source</a>
 * </p>
 *
 * @author rfischer
 * @version $Date: May 10, 2007 8:07:29 AM $
 */
public class UnboundedPipeLink<T> extends PipeLink<T> {

    /**
     * Creates a new intance of <code>UnboundedPipeLink</code>.
     */
    public UnboundedPipeLink() {
        super(new LinkedBlockingQueue<T>());
    }

    /**
     * Have to override this because
     * {@link LinkedBlockingQueue#remainingCapacity()} doesn't work as expected --
     * apparently an unbounded queue runs low on space after you insert
     * elements.
     * <p/>
     * {@inheritDoc}
     */
    @Override
    public int getRemainingCapacity() {
        return Integer.MAX_VALUE;
    }
}
