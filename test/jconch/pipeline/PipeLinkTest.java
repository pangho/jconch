package jconch.pipeline;

import static org.junit.Assert.assertTrue;
import static test.utils.MemoryTestUtils.forceGC;

import java.util.concurrent.SynchronousQueue;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Tests for {@link PipeLink} instances.
 */
public class PipeLinkTest {

    /**
     * Intended to be overriden by child classes to specify which child class to
     * test.
     * 
     * @return A newly created PipeLink to test.
     */
    protected PipeLink<Object> createFixture() {
        return new PipeLink<Object>(new SynchronousQueue<Object>());
    }

    @Test(expected = NullArgumentException.class)
    public void pipeLinkConstructorExplodesOnNullArg() {
        new PipeLink<Object>(null);
    }

    @Test(expected = NullArgumentException.class)
    public void registerSourceExplodesOnNull() {
        createFixture().registerSource(null);
    }

}
