package jconch.pipeline;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.mutable.MutableLong;

/**
 * <p>
 * An unbounded link in the pipeline. This is a very fast implementation of a
 * link, but allows potentially unlimited elements to build up, which can be
 * counter-productive to the multithreaded approach.
 * </p>
 * <p>
 * This class has a concept of a "fetch timeout" ({@link #getFetchTimeout()}/{@link #setFetchTimeout(long)}).
 * Although adding new elements will always be accepted, it is possible that a
 * stall might cause a delay in processing. The fetch timeout is the amount of
 * time to wait before the stall is detected. The default value is
 * <code>0</code>, which means to not wait at all, but act in pass-through
 * mode.
 * </p>
 * 
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
}
