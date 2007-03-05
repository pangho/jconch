package com.fischerventure.jconch.lock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.fischerventure.jconch.FrameworkTest;

public abstract class AbstractLogEqLockTest<T extends AbstractLogEqLock>
		extends FrameworkTest {

	protected abstract T createTestInstance();

	@Test
	public void testCreateNewLockAlwaysCreatesANewLock() {
		// Make sure that we're generating different locks all the time.
		final int testingIterations = 1000;
		final T lockMaker = createTestInstance();
		assertNotNull(lockMaker);
		final List<Object> lockLists = new ArrayList<Object>(
				testingIterations + 1);
		for (int i = 0; i < testingIterations; i++) {
			final Object nextLock = lockMaker.createNewLock();
			assertNotNull(nextLock);
			assertFalse(lockLists.contains(nextLock));
			lockLists.add(lockLists);
		}
	}

	@Test
	public void testHoldsOntoLock() {
		// Make sure that we hold onto the same lock no matter how many times we
		// call the method. There was a bug where we lost locks at about 2000,
		// which dropped to 6 when I shrank the memory size.
		final int testingIterations = 50000;
		final T lockMaker = createTestInstance();
		assertNotNull(lockMaker);
		final Integer testIn = new Integer(Integer.MAX_VALUE);
		final Object lock = lockMaker.getLock(testIn);
		for (int i = 0; i < testingIterations; i++) {
			final Integer testTwo = new Integer(testIn.intValue());
			assertEquals(testIn, testTwo);
			assertNotSame(testIn, testTwo);
			assertSame("Got a different lock on iteration " + (i + 1), lock,
					lockMaker.getLock(testTwo));
		}
	}

	@Test
	public void makeSureNewKeyDoesNotLoseLock() {
		final int val = Integer.MIN_VALUE;
		final T lockMaker = createTestInstance();

		// Get a lock whose key could be GC'ed.
		final Object lock1 = lockMaker.getLock(new Integer(val));
		final Integer val2 = new Integer(val);
		final Object lock2 = lockMaker.getLock(val2);
		assertNotNull(lock1);
		assertSame(lock1, lock2);

		// Now force a full GC
		forceGC();

		// Now make sure that we still get lock2 for val2.
		final Object lock2_2 = lockMaker.getLock(val2);
		assertSame(lock2, lock2_2);
	}

	/**
	 * When this method returns, there has been a full garbage collection.
	 */
	protected final void forceGC() {
		// Set up what we're looking for
		final ReferenceQueue<Object> q = new ReferenceQueue<Object>();
		final Reference ref = new SoftReference<Object>(new Object(), q);

		// Force a GC
		boolean sawGC = false;
		final List<Object> list = new ArrayList<Object>();
		list.add(ref);
		for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
			System.gc(); // Request a GC: non-mandatory
			if (sawGC || q.poll() != null) {
				// Gotcha!!
				sawGC = true;
				break;
			} else {
				list.add(new Date());
			}
		}
		list.clear();
		System.gc(); // Because we just deleted a huge chunk
		return;
	}

	@Test
	public void doubleCheckWeHaveALockWhenWeHaveALock() {
		final T lockMaker = createTestInstance();
		assertNotNull(lockMaker);
		final String value = "This is a random string!";
		final Object lock1 = lockMaker.getLock(value);
		assertNotNull(lock1);
		for (int i = 0; i < 2; i++) {
			assertTrue(lockMaker.hasLockFor(value));
			final Object lock2 = lockMaker.getLock(value);
			assertNotNull(lock2);
			assertSame(lock1, lock2);
			forceGC();
		}
	}

	@Test
	public void verifyLosingDataBug() {
		// I thought this was causing a bug, but it's not.
		final T lockMaker = createTestInstance();
		assertNotNull(lockMaker);
		final Integer dataOne = new Integer(1);
		final Object lockOne = lockMaker.getLock(dataOne);
		lockMaker.getLock(new Integer(dataOne.intValue()));
		forceGC();
		final Object lockTwo = lockMaker
				.getLock(new Integer(dataOne.intValue()));
		final Object lockOneTwo = lockMaker.getLock(dataOne);
		assertSame("Lock for new data after GC is different", lockOne, lockTwo);
		assertSame("Lock for old data after GC is different", lockOne,
				lockOneTwo);
	}

	@Test
	public void tryForTheLosingDataBugAgain() {
		// I thought this was causing a bug, but it's not.
		final T lockMaker = createTestInstance();
		assertNotNull(lockMaker);
		final Integer dataOne = new Integer(1);
		final Object lockOne = lockMaker.getLock(dataOne);
		Integer dataTwo = new Integer(dataOne.intValue());
		final Object lockTwo = lockMaker.getLock(dataTwo);
		assertSame("Lock one and lock two are different", lockOne, lockTwo);
		dataTwo = null;
		forceGC();
		final Object lockThree = lockMaker.getLock(new Integer(dataOne
				.intValue()));
		assertSame("Lock for new data after GC is different", lockOne,
				lockThree);
		forceGC();
		final Object lockOneTwo = lockMaker.getLock(dataOne);
		assertSame("Lock for old data after GC is different", lockOne,
				lockOneTwo);
	}

}
