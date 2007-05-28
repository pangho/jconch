package jconch.pipeline;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import jconch.pipeline.impl.CollectionConsumer;
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
		// Set up the scenario
		final Object first = new Object();
		final Object second = null;
		final PipeLink<Object> link = createMock(PipeLink.class);
		expect(link.get()).andReturn(first);
		expect(link.get()).andReturn(second);
		replay(link);

		// Now test the functionality
		final CollectionConsumer<Object> fixture = new CollectionConsumer<Object>(
				new InlineThreadingModel(), link) {
			@Override
			public void logMessage(String msg, Exception e) {
				// Do nothing
			}
		};
		fixture.execute();
		assertEquals("Collection is not the right size after first execute", 1,
				fixture.getCollection().size());
		assertTrue("Collection does not contain first element", fixture
				.getCollection().contains(first));
		fixture.execute();
		assertEquals("Collection is not the right size after second execute",
				1, fixture.getCollection().size());

		// See if we get the explosion
		Exception ex = null;
		try {
			fixture.execute();
		} catch (IllegalStateException ise) {
			ex = ise;
		}
		assertNotNull("Did not see exception on third execute", ex);

		// Double-check the scenario ran right
		verify(link);
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
