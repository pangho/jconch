package jconch.pipeline;

import static org.junit.Assert.*;

import java.util.Arrays;

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
		final Producer fixture = new NopProducer(new InlineThreadingModel(),
				link);
		assertEquals(link, fixture.getLinkOut());
	}

	@Test
	public void creatingNullSignalsFinished() {
		final Object first = new Object();
		final Producer producer = new CollectionProducer<Object>(Arrays.asList(
				first, null), new InlineThreadingModel(),
				new UnboundedPipeLink<Object>()) {
			@Override
			public void logMessage(String msg, Exception e) {
				// Ignore
			}
		};
		assertFalse("Started out finished already", producer.isFinished());
		assertSame("Got different values for test production", first, producer
				.produceItem());
		assertFalse("Finished after first item", producer.isFinished());
		assertNull("Did not get null when expected", producer.produceItem());
		assertTrue("Is not finished when it should be", producer.isFinished());
		IllegalStateException exception = null;
		try {
			producer.produceItem();
			fail("Should have exploded when producing item");
		} catch (IllegalStateException ise) {
			exception = ise;
		}
		assertNotNull("Did not catch the exception", exception);
	}

	public class NopProducer extends Producer<Object> {

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
