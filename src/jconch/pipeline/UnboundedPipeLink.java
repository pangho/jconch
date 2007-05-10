/*
 * Project: JConch
 * @(#)$Id:  UnboundedPipeLink.java May 10, 2007 8:07:30 AM Robert Fischer $
 * 
 * This Software is confidential and copyrighted.
 * Copyright  2003, 2007 Room & Board, Inc.  All Rights Reserved.
 * 
 * This software is the proprietary information of Room & Board, Inc.  Use 
 * is subject to license terms.
 * 
 * Title to Software and all associated intellectual property rights is retained 
 * by Room & Board, Inc. and/or its licensors.
 * 
 * Except as specifically authorized in any Supplemental License Terms, this 
 * software may not be copied.  Unless enforcement is prohibited by 
 * applicable law, you may not modify, decompile, reverse engineer this 
 * Software without the express written permission of Room & Board, Inc.
 * 
 * No right, title or interest in or to any trademark, service mark, logo or 
 * trade name of Room & Board, Inc. or its licensors is granted under 
 * this Agreement.
 */
package jconch.pipeline;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

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
     * The timeout on fetches.
     */
    private MutableLong fetchTimeout = new MutableLong(0L);

    /**
     * Creates a new intance of <code>UnboundedPipeLink</code>.
     * 
     * @param queue
     */
    protected UnboundedPipeLink() {
        super(new ConcurrentLinkedQueue<T>());
    }

    /**
     * Gets the timeout on fetch operations of the instance.
     * 
     * @return the fetch timeout.
     */
    public long getFetchTimeout() {
        synchronized (fetchTimeout) {
            return fetchTimeout.longValue();
        }
    }

    /**
     * Sets the fetchTimeout of the instance. Positive values represent
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
        synchronized (fetchTimeout) {
            fetchTimeout.setValue(newFetchTimeout);
        }
    }

    /**
     * In addition to the underlying behavior, this method acts on the fetch
     * timeout setting (see the class documentation).
     * 
     * {@inheritDoc}
     */
    @Override
    public T get() {
        // TODO This logic should get promoted

        // See if we get lucky and can bypass all the timeout logic
        final T attempt = super.get();

        // Did we get something?
        if (attempt == null) {

            // Drat. Okay, best to wait on the timeout
            final long startTime = System.currentTimeMillis();
            final long fullTimeout = getFetchTimeout();
            for (long timeout = fullTimeout; timeout > 0; timeout = fullTimeout
                    - (System.currentTimeMillis() - startTime)) {
                try {
                    boundary.wait(timeout);
                } catch (InterruptedException ie) {
                    // Do nothing
                }

                // Try to get something again
                final T attempt2 = super.get();
                if (attempt2 != null) {
                    return attempt2;
                }
            }

            // Didn't get anything in the timeout
            return null;

        } else {

            // GOt something right off the bat. Sweet!
            return attempt;

        }
    }
}
