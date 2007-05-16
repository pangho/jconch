package jconch.pipeline;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.lang.NullArgumentException;
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
        return new PipeLink<Object>(new ArrayBlockingQueue<Object>(1));
    }

    @Test(expected = NullArgumentException.class)
    public void pipeLinkConstructorExplodesOnNullArg() {
        new PipeLink<Object>(null);
    }

    @Test(expected = NullArgumentException.class)
    public void registerSourceExplodesOnNull() {
        createFixture().registerSource(null);
    }

    @Test
    public void runAnElementThrough() {
        final Object testMonkey = new Object();
        final PipeLink<Object> fixture = createFixture();
        assertTrue("Add failed", fixture.add(testMonkey));
        final Object outMonkey = fixture.get();
        assertNotNull("Retrieved null", outMonkey);
        assertSame("Retrieved something different", testMonkey, outMonkey);
    }

    @Test
    public void breakLinkBreaksAdd() {
        final Object testMonkey = new Object();
        final PipeLink<Object> fixture = createFixture();
        fixture.breakLink();
        assertFalse("Add should have failed", fixture.add(testMonkey));
    }

    @Test
    public void breakLinkBreaksGet() {
        final Object testMonkey = new Object();
        final PipeLink<Object> fixture = createFixture();
        assertTrue("Add failed", fixture.add(testMonkey));
        fixture.breakLink();
        final Object outMonkey = fixture.get();
        assertNull("Retrieved an object -- should be null", outMonkey);
    }

    @Test
    public void setAddTimeoutDoesNotExplodeOnZero() {
        createFixture().setAddTimeout(0);
    }

    @Test
    public void setFetchTimeoutDoesNotExplodeOnZero() {
        createFixture().setFetchTimeout(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAddTimeoutExplodesOnNegative() {
        createFixture().setAddTimeout(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFetchTimeoutExplodesOnNegative() {
        createFixture().setFetchTimeout(-1);
    }

}
