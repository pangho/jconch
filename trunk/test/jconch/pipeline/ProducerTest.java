package jconch.pipeline;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import jconch.pipeline.impl.CollectionProducer;
import jconch.pipeline.impl.InlineThreadingModel;
import jconch.pipeline.impl.UnboundedPipeLink;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class ProducerTest {

    @Test(expected = NullArgumentException.class)
    public void constructorExplodesOnFirstNull() {
        new NopProducer(null, new UnboundedPipeLink<Object>());
    }

    @Test(expected = NullArgumentException.class)
    public void constructorExplodesOnSecondNull() {
        new NopProducer(new InlineThreadingModel(), null);
    }

    @Test
    public void getLinkOutGetsTheLinkPassedIntoTheConstructor() {
        final PipeLink<Object> link = new UnboundedPipeLink<Object>();
        final Producer fixture = new NopProducer(new InlineThreadingModel(), link);
        assertEquals(link, fixture.getLinkOut());
    }

    @Test
    public void creatingNullSignalsFinished() {
        final Object first = new Object();
        final PipeLink<Object> outLink = new UnboundedPipeLink<Object>();
        final Producer producer = new NoLoggingCollectionProducer(Arrays.asList(first, null),
                new InlineThreadingModel(), outLink);
        assertFalse("Started out finished already", producer.isFinished());
        producer.execute();
        assertSame("Got different values for test production", first, outLink.get());
        assertFalse("Finished after first item", producer.isFinished());
        producer.execute();
        assertEquals("Got another element we didn't expect to see", 0, outLink.getQueueLength());
        assertTrue("Is not finished when it should be", producer.isFinished());
        IllegalStateException exception = null;
        try {
            producer.execute();
            fail("Should have exploded when producing item");
        } catch (IllegalStateException ise) {
            exception = ise;
        }
        assertNotNull("Did not catch the exception", exception);
    }

    @Test
    public void failingAddSignalsFinished() {
        // Create the test objects moving through
        final Object first = new Object();
        final Object second = new Object();
        final Object third = new Object();

        // Going to use mock objects to handle this
        // It's going to return true for add twice, and then fail on the third
        // attempt. It's that third attempt we're testing.
        final PipeLink<Object> outLink = (PipeLink<Object>) createNiceMock(PipeLink.class);
        expect(outLink.add(first)).andReturn(true);
        expect(outLink.add(second)).andReturn(true);
        expect(outLink.add(third)).andReturn(false);
        replay(outLink);

        // Now build up the fixture.
        final Producer producer = new NoLoggingCollectionProducer(Arrays.asList(first, second, third),
                new InlineThreadingModel(), outLink);

        // Run the scenario
        assertFalse("Started out finished already", producer.isFinished());
        producer.execute();
        assertFalse("Finished after first item", producer.isFinished());
        producer.execute();
        assertFalse("Finished after second item", producer.isFinished());
        producer.execute();
        assertTrue("Is not finished when it should be", producer.isFinished());
        IllegalStateException exception = null;
        try {
            producer.execute();
            fail("Should have exploded when producing item");
        } catch (IllegalStateException ise) {
            exception = ise;
        }
        assertNotNull("Did not catch the exception", exception);
        verify(outLink);
    }

    @Test
    public void isExhaustedSignalsIsFinished() {
        // Create the test objects moving through
        final Object first = new Object();
        final Object second = new Object();
        final Object third = new Object();

        // Going to use mock objects to handle this
        // It's going to return true for add twice, and then fail on the third
        // attempt. It's that third attempt we're testing.
        final PipeLink<Object> outLink = (PipeLink<Object>) createNiceMock(PipeLink.class);
        expect(outLink.add(first)).andReturn(true);
        expect(outLink.add(second)).andReturn(true);
        expect(outLink.add(third)).andReturn(true);
        replay(outLink);

        // Now build up the fixture.
        final Producer producer = new NoLoggingCollectionProducer(Arrays.asList(first, second, third),
                new InlineThreadingModel(), outLink);

        // Run the scenario
        assertFalse("Started out finished already", producer.isFinished());
        producer.execute();
        assertFalse("Finished after first item", producer.isFinished());
        producer.execute();
        assertFalse("Finished after second item", producer.isFinished());
        producer.execute();
        assertTrue("Is not exhausted when it should be", producer.isExhausted());
        assertTrue("Is not finished when it should be", producer.isFinished());
        IllegalStateException exception = null;
        try {
            producer.execute();
            fail("Should have exploded when producing item");
        } catch (IllegalStateException ise) {
            exception = ise;
        }
        assertNotNull("Did not catch the exception", exception);
        verify(outLink);
    }

    @Test
    public void explodingAddSignalsIsFinished() {
        // Create the test objects moving through
        final Object first = new Object();
        final Object second = new Object();
        final Object third = new Object();

        // Going to use mock objects to handle this
        // It's going to return true for add twice, and then fail on the third
        // attempt. It's that third attempt we're testing.
        final PipeLink<Object> outLink = (PipeLink<Object>) createNiceMock(PipeLink.class);
        expect(outLink.add(first)).andReturn(true);
        expect(outLink.add(second)).andThrow(new RuntimeException("Boom!"));
        replay(outLink);

        // Now build up the fixture.
        final Producer producer = new NoLoggingCollectionProducer(Arrays.asList(first, second, third),
                new InlineThreadingModel(), outLink);

        // Run the scenario
        assertFalse("Started out finished already", producer.isFinished());
        producer.execute();
        assertFalse("Finished after first item", producer.isFinished());
        producer.execute();
        assertTrue("Is not finished when it should be", producer.isFinished());
        IllegalStateException exception = null;
        try {
            producer.execute();
            fail("Should have exploded when producing item");
        } catch (IllegalStateException ise) {
            exception = ise;
        }
        assertNotNull("Did not catch the exception", exception);
        verify(outLink);

    }

    protected static class NoLoggingCollectionProducer extends CollectionProducer<Object> {

        public NoLoggingCollectionProducer(Collection<Object> data, ThreadingModel model, PipeLink<Object> out) {
            super(data, model, out);
        }

        @Override
        public void logMessage(String msg, Exception e) {
            // Does nothing
        }

    }

    protected static class NopProducer extends Producer<Object> {

        public NopProducer(ThreadingModel threading, PipeLink<Object> link) {
            super(threading, link);
        }

        @Override
        public boolean isExhausted() {
            return false;
        }

        @Override
        public Object produceItem() {
            return null;
        }

        @Override
        public void logMessage(String msg, Exception e) {
        }
    }
}
