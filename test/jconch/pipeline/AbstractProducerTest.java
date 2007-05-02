package jconch.pipeline;

import static org.junit.Assert.*;
import jconch.test.FrameworkTest;

import org.junit.Before;
import org.junit.Test;

/**
 * A generic base test for implementations of {@link Producer}.
 * 
 * @author Robert Fischer
 */
public abstract class AbstractProducerTest<PRODUCER_T extends Producer> extends FrameworkTest {

    /**
     * The object undergoing testing.
     */
    protected PRODUCER_T fixture;

    /**
     * Provides the implementation for creating fixtures for the given pipe
     * link.
     * 
     * @return A newly-created fixture to test.
     */
    public abstract PRODUCER_T createFixture(final PipeLink link);

    /**
     * Assign the fixture and do some validation on it.
     */
    @Before
    public final void assignFixture() {
        final PipeLink link = new PipeLink();
        fixture = createFixture(link);
        assertNotNull("Fixture was not created", fixture);
        assertSame("Different link is being used", link, fixture.getLinkOut());
        assertTrue("Link is not empty", link.isEmpty());
    }
}
