package jconch.pipeline;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import jconch.pipeline.impl.InlineThreadingModel;
import jconch.pipeline.impl.UnboundedPipeLink;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

/**
 * Tests for {@link Consumer}.
 * 
 * @author Robert
 */
public class ConsumerTest {

	@Test(expected = NullArgumentException.class)
	public void constructorExplodesOnFirstNull() {
		new NopConsumer(null, new UnboundedPipeLink<Object>());
	}

	@Test(expected = NullArgumentException.class)
	public void constructorExplodesOnSecondNull() {
		new NopConsumer(new InlineThreadingModel(), null);
	}

	@Test
	public void constructorSetsThePipeLink() {
		final PipeLink<Object> link = new UnboundedPipeLink<Object>();
		assertEquals("Different link found", link, new NopConsumer(
				new InlineThreadingModel(), link).getInPipeLink());
	}

	@Test
	public void explodesIfExecuteCalledAfterSeeingNull() {
		final PipeLink<Object> link = createMock(PipeLink.class);
	}

	/**
	 * Simple pass-through subclass.
	 */
	private static final class NopConsumer extends Consumer<Object> {

		public NopConsumer(final ThreadingModel threading, final PipeLink in) {
			super(threading, in);
		}

		@Override
		public void consumeItem(final Object item) {
			// Do nothing
		}

		@Override
		public void logMessage(final String msg, final Exception e) {
			// Do nothing
		}
	}
}
