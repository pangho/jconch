package jconch.lock;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import jconch.lock.RWLogEqLock;
import jconch.lock.SyncLogEqLock;

import org.junit.Test;

/**
 * Tests for {@link RWLogEqLock}.
 * 
 * @author Robert Fischer
 */
public class RWLogEqLockTest extends AbstractLogEqLockTest<RWLogEqLock> {

	@Test
	public void testGetGlobalInstance() {
		final int iterations = 10000;
		final int maxWaitPerThread = 100;
		final SyncLogEqLock global = SyncLogEqLock.getGlobalInstance();
		assertNotNull(global);

		// Now check to see if other threads ever see something different
		final AtomicBoolean sawSomethingDifferent = new AtomicBoolean(false);
		for (int i = 0; i < iterations; i++) {
			final int waitBeforeRetrieve = new Random()
					.nextInt(maxWaitPerThread) + 1;
			assertFalse(0 == waitBeforeRetrieve);
			final Runnable createAndCheck = new Runnable() {
				public void run() {
					// Wait some arbitrary time
					try {
						Thread.sleep(waitBeforeRetrieve);
					} catch (InterruptedException ie) {
						Thread.yield();
					}

					// Now check to see if we get the same global
					final SyncLogEqLock myGlobal = SyncLogEqLock
							.getGlobalInstance();
					if (myGlobal != global) {
						sawSomethingDifferent.set(true);
					}
				}
			};
			new Thread(createAndCheck).start();
			assertFalse("Saw something different", sawSomethingDifferent.get());
		}

		// Give things a chance to finish up.
		try {
			Thread.sleep(10 * maxWaitPerThread);
		} catch (InterruptedException ie) {
			Thread.yield();
		}
		assertFalse("Saw something different", sawSomethingDifferent.get());
	}

	@Override
	protected RWLogEqLock createTestInstance() {
		return new RWLogEqLock();
	}

}
