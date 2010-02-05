package jconch.lock;

import static org.testng.AssertJUnit.*;
import static test.utils.MemoryTestUtils.*;

import java.util.*;

import jconch.test.FrameworkTest;

import org.testng.annotations.Test;

import test.utils.MemoryTestUtils;

public abstract class AbstractLogEqLockTest<LOCK_T, T extends AbstractLogEqLock<Long, LOCK_T>> extends FrameworkTest {

    protected abstract T createTestInstance();

    @Test
    public void generateObjectDoesNotGenerateSameObject() {
        final int iters = 10000;
        final Collection<Long> generated = new HashSet<Long>(iters, .1f);
        for (int i = 0; i < iters; i++) {
            final Long iterObj = this.generateNewValue();
            assertNotNull("Generated a null value", iterObj);
            assertFalse("Already saw generated value", generated.contains(iterObj));
            generated.add(iterObj);
        }
    }

    @Test(invocationCount = 2)
    public void doubleCheckWeHaveALockWhenWeHaveALock() {
        final T lockMaker = this.createTestInstance();
        assertNotNull(lockMaker);
        final Long value = this.generateNewValue();
        final LOCK_T lock1 = lockMaker.getLock(value);
        assertNotNull(lock1);
        for (int i = 0; i < 2; i++) {
            assertTrue(lockMaker.hasLockFor(value));
            final LOCK_T lock2 = lockMaker.getLock(value);
            assertNotNull(lock2);
            assertSame(lock1, lock2);
            forceGC(); // This takes a LONG time
        }
    }

    protected final Long generateNewValue() {
        try {
            Thread.sleep(2L);
        } catch (final InterruptedException e) {
            MemoryTestUtils.forceGC();
            Thread.yield();
        }
        return System.currentTimeMillis();
    }

    @Test
    public void hasLockForDoesNotCreateLockFor() {
        final T lockMaker = this.createTestInstance();
        assertNotNull(lockMaker);
        final Long toCheck = this.generateNewValue();
        assertFalse(lockMaker.hasLockFor(toCheck));
        assertFalse(lockMaker.hasLockFor(toCheck));
    }

    @Test
    public void makeSureNewKeyDoesNotLoseLock() {
        final T lockMaker = this.createTestInstance();
        final long val = this.generateNewValue().longValue();

        // Get a lock whose key could be GC'ed.
        final LOCK_T lock1 = lockMaker.getLock(new Long(val));
        final Long val2 = new Long(val);
        final LOCK_T lock2 = lockMaker.getLock(val2);
        assertNotNull(lock1);
        assertSame(lock1, lock2);

        // Now force a full GC
        // (This takes a LOT of time, and it's why we're ignoring the test)
        forceGC();

        // Now make sure that we still get lock2 for val2.
        final LOCK_T lock2_2 = lockMaker.getLock(val2);
        assertSame(lock2, lock2_2);
    }

    @Test
    public void testCreateNewLockAlwaysCreatesANewLock() {
        // Make sure that we're generating different locks all the time.
        final int testingIterations = 1000;
        final T lockMaker = this.createTestInstance();
        assertNotNull(lockMaker);
        final List<Object> lockLists = new ArrayList<Object>(testingIterations + 1);
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
        final T lockMaker = this.createTestInstance();
        assertNotNull(lockMaker);
        final Long testIn = this.generateNewValue();
        final LOCK_T lock = lockMaker.getLock(testIn);
        for (int i = 0; i < testingIterations; i++) {
            final Long testTwo = new Long(testIn.longValue());
            assertEquals(testIn, testTwo);
            assertNotSame(testIn, testTwo);
            assertSame("Got a different lock on iteration " + (i + 1), lock, lockMaker.getLock(testTwo));
        }
    }

    @Test
    public void tryForTheLosingDataBugAgain() {
        // I thought this was causing a bug, but it's not.
        final T lockMaker = this.createTestInstance();
        assertNotNull(lockMaker);
        final Long dataOne = this.generateNewValue();
        final LOCK_T lockOne = lockMaker.getLock(dataOne);
        Long dataTwo = new Long(dataOne.longValue());
        final LOCK_T lockTwo = lockMaker.getLock(dataTwo);
        assertSame("Lock one and lock two are different", lockOne, lockTwo);
        dataTwo = null;
        forceGC();
        final LOCK_T lockThree = lockMaker.getLock(new Long(dataOne.longValue()));
        assertSame("Lock for new data after GC is different", lockOne, lockThree);
        forceGC();
        final LOCK_T lockOneTwo = lockMaker.getLock(dataOne);
        assertSame("Lock for old data after GC is different", lockOne, lockOneTwo);
    }

    @Test
    public void verifyLosingDataBug() {
        // I thought this was causing a bug, but it's not.
        final T lockMaker = this.createTestInstance();
        assertNotNull(lockMaker);
        final Long dataOne = generateNewValue();
        final LOCK_T lockOne = lockMaker.getLock(dataOne);
        lockMaker.getLock(new Long(dataOne.longValue()));
        forceGC();
        final LOCK_T lockTwo = lockMaker.getLock(new Long(dataOne.longValue()));
        final LOCK_T lockOneTwo = lockMaker.getLock(dataOne);
        assertSame("Lock for new data after GC is different", lockOne, lockTwo);
        assertSame("Lock for old data after GC is different", lockOne, lockOneTwo);
    }

}
