package jconch.pipeline;

import static org.testng.AssertJUnit.*;

import java.util.*;

import jconch.pipeline.impl.CollectionProducer;
import jconch.pipeline.impl.ExceptionThreadingModel;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.time.DateUtils;
import org.testng.annotations.Test;

/**
 * Tests for {@link PipeLink} instances.
 */
public abstract class AbstractPipeLinkTest<LINK_T extends PipeLink<Object>> {

	@Test(expectedExceptions = NullArgumentException.class)
	public void addExplodesOnNull() {
		this.createFixture().add(null);
	}

	@Test
	public void breakLinkBreaksAdd() {
		final Object testMonkey = new Object();
		final PipeLink<Object> fixture = this.createFixture();
		fixture.breakLink();
		assertFalse("Add should have failed", fixture.add(testMonkey));
	}

	@Test
	public void breakLinkBreaksGet() {
		final Object testMonkey = new Object();
		final PipeLink<Object> fixture = this.createFixture();
		assertTrue("Add failed", fixture.add(testMonkey));
		fixture.breakLink();
		final Object outMonkey = fixture.get();
		assertNull("Retrieved an object -- should be null", outMonkey);
	}

	/**
	 * Intended to be overriden by child classes to specify which child class to
	 * test.
	 * 
	 * @return A newly created PipeLink to test.
	 */
	protected abstract LINK_T createFixture();

	@Test
	public void demonstrateFetchNoTimeoutWithNoSources() {
		final LINK_T fixture = this.createFixture();
		final long timeout = DateUtils.MILLIS_PER_SECOND;
		fixture.setFetchTimeout(timeout);
		final long startTime = System.currentTimeMillis();
		final Object monkeyOut = fixture.get();
		assertTrue("Looks like it waited",
				System.currentTimeMillis() < startTime + timeout);
		assertNull("Got a value back", monkeyOut);
	}

	@Test
	public void demonstrateFetchTimeoutWithFailedGet() {
		final LINK_T fixture = this.createFixture();
		final Producer<Object> source = this.tokenProducer(fixture);
		assertNotNull("Source was null", source);
		final long timeout = DateUtils.MILLIS_PER_SECOND;
		fixture.setFetchTimeout(timeout);
		final long startTime = System.currentTimeMillis();
		final Object monkeyOut = fixture.get();
		assertTrue("Did not wait long enough",
				System.currentTimeMillis() * 1.1 >= startTime + timeout);
		assertNull("Got a value back", monkeyOut);
	}

	@Test
	public void demonstrateFetchTimeoutWithSuccessfulGet() {
		final long timeout = DateUtils.MILLIS_PER_SECOND;
		final Object monkey = new Object();
		final LINK_T fixture = this.createFixture();
		final Producer<Object> source = this.tokenProducer(fixture);
		assertNotNull("Source is null", source);
		fixture.setFetchTimeout(timeout);
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout / 2);
				} catch (final InterruptedException e) {
					Thread.yield();
				}
				fixture.add(monkey);
			}
		}.start();
		final Object monkeyOut = fixture.get();
		assertSame("Did not get the same test item out", monkey, monkeyOut);
	}

	@Test
	public void demonstratePutTimeoutWithFailedAdd() {
		// This test doesn't make sense for unbounded links
		if (this.getMaxCapacity() == Integer.MAX_VALUE) {
			return;
		}

		final long timeout = DateUtils.MILLIS_PER_SECOND;

		// Create the fixture
		final LINK_T fixture = this.createFixture();
		fixture.setAddTimeout(timeout);

		// Add a source to keep the shortcut from hitting
		final Producer source = this.tokenProducer(fixture);
		assertNotNull("Source was null", source);

		// Saturate the link
		for (int i = 0; i < this.getMaxCapacity(); i++) {
			assertTrue("Add did not succeed when saturating pipe", fixture
					.add(new Object()));
		}

		// Now check the timing
		final long startTime = System.currentTimeMillis();
		assertFalse("Add succeeded", fixture.add(new Object()));
		assertTrue("Did not wait long enough: waited "
				+ (System.currentTimeMillis() - startTime) + "ms", System
				.currentTimeMillis() * 1.1 >= startTime + timeout);
	}

	@Test
	public void demonstratePutTimeoutWithSuccessfulAdd() {
		// This test doesn't make sense for unbounded links
		if (this.getMaxCapacity() == Integer.MAX_VALUE) {
			return;
		}

		final long timeout = DateUtils.MILLIS_PER_SECOND;

		// Create the fixture
		final LINK_T fixture = this.createFixture();
		fixture.setAddTimeout(timeout);

		// Saturate the link
		for (int i = 0; i < this.getMaxCapacity(); i++) {
			assertTrue("Add did not succeed when saturating pipe", fixture
					.add(new Object()));
		}

		// Set off the thing that will fetch the thread
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(timeout / 2);
				} catch (final InterruptedException e) {
					Thread.yield();
				}
				fixture.get();
			}
		}.start();
		assertTrue("Add did not succeed", fixture.add(new Object()));
	}

	@Test
	public void demonstrateQueueLengthRemainingCapacityAndClear() {
		final boolean isBounded = this.getMaxCapacity() != Integer.MAX_VALUE;
		final LINK_T fixture = this.createFixture();
		assertEquals("Empty fixture does not have 0 length", 0, fixture
				.getQueueLength());
		assertEquals("Empty fixture does not have capacity equal to capacity",
				this.getMaxCapacity(), fixture.getRemainingCapacity());
		final int testSize = isBounded ? this.getMaxCapacity() : 10000;
		for (int i = 0; i < testSize; i++) {
			fixture.add(new Object());
		}
		assertEquals("Fixture does not report the correct number of elements",
				testSize, fixture.getQueueLength());
		if (isBounded) {
			assertEquals("Fixture reports more space after saturation", 0,
					fixture.getRemainingCapacity());
		} else {
			assertEquals("Fixture reports a bound on space after saturation",
					Integer.MAX_VALUE, fixture.getRemainingCapacity());
		}
		fixture.clearQueue();
		assertEquals("Fixture reports a queue length after clear", 0, fixture
				.getQueueLength());
		assertEquals(
				"Fixture does not have capacity equal to capacity after clear",
				this.getMaxCapacity(), fixture.getRemainingCapacity());
		assertNull("Fixture returned an element after clear", fixture.get());
	}

	/**
	 * Provides the maximum capacity for the fixture.
	 * 
	 * @return The capacity of the fixture, or {@link Integer#MAX_VALUE} if
	 *         unbounded.
	 */
	protected abstract int getMaxCapacity();

	@Test(expectedExceptions = NullArgumentException.class)
	public void registerSourceExplodesOnNull() {
		this.createFixture().registerSource(null);
	}

	@Test
	public void runAnElementThrough() {
		final Object testMonkey = new Object();
		final PipeLink<Object> fixture = this.createFixture();
		assertTrue("Add failed", fixture.add(testMonkey));
		final Object outMonkey = fixture.get();
		assertNotNull("Retrieved null", outMonkey);
		assertSame("Retrieved something different", testMonkey, outMonkey);
	}

	@Test
	public void runManyElementsThroughDumpAndDraw() {
		final int testSize = 100000;
		final LINK_T fixture = this.createFixture();
		for (int i = 0; i < testSize; i++) {
			final Object monkey = new Object();
			assertTrue("Add failed", fixture.add(monkey));
			final Object monkeyOut = fixture.get();
			assertNotNull("Got null back", monkeyOut);
			assertSame("Got something weird back", monkey, monkeyOut);
			final Object emptyCheck = fixture.get();
			assertNull("Got an extra element back", emptyCheck);
		}
	}

	@Test
	public void runManyElementsThroughDumpThenDraw() {
		final int testSize = Math.min(this.getMaxCapacity(), 10000);
		final Collection<Object> eltsIn = new ArrayList<Object>(testSize + 1);
		for (int i = 0; i < testSize; i++) {
			eltsIn.add(testSize);
		}
		final LINK_T fixture = this.createFixture();
		for (final Object in : eltsIn) {
			assertTrue("Failed on add", fixture.add(in));
		}
		for (int i = 0; i < testSize; i++) {
			final Object out = fixture.get();
			assertNotNull("Generated a null object", out);
			// We don't want to test ordering here: not part of the API
			assertTrue("Did not recognize object we got back (duplicate?)",
					eltsIn.contains(out));
			eltsIn.remove(out);
		}
		assertTrue("Did not see all the items we expected to see", eltsIn
				.isEmpty());
		final Object lastOut = fixture.get();
		assertNull("Saw an additional item", lastOut);
	}

	@Test
	public void setAddTimeoutDoesNotExplodeOnZero() {
		this.createFixture().setAddTimeout(0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setAddTimeoutExplodesOnNegative() {
		this.createFixture().setAddTimeout(-1);
	}

	@Test
	public void setFetchTimeoutDoesNotExplodeOnZero() {
		this.createFixture().setFetchTimeout(0);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void setFetchTimeoutExplodesOnNegative() {
		this.createFixture().setFetchTimeout(-1);
	}

	/**
	 * Creates a producer that can't actually produce anything, but can be used
	 * to populate {@link PipeLink#sources}.
	 * 
	 * @return A producer which is never finished, and will not allows
	 *         {@link Producer#start()} to be called.
	 */
	protected Producer<Object> tokenProducer(final LINK_T fixture) {
		final PipeLink<Object> link = fixture;
		return new CollectionProducer<Object>(Arrays
				.asList(new Object[] { new Object() }),
				new ExceptionThreadingModel(), link) {
			@Override
			public void logMessage(final String msg, final Exception e) {
				return;
			}
		};
	}

	@Test
	public void validateCreateFixture() {
		assertNotNull("Created a null fixture", this.createFixture());
		assertNotSame("Created the same fixture twice", this.createFixture(),
				this.createFixture());
		assertTrue("Fixture is created with a source",
				this.createFixture().sources.isEmpty());
	}

	@Test
	public void validateMaxCapacity() {
		assertTrue("Max capacity is nonpositive", this.getMaxCapacity() > 0);
	}
}
