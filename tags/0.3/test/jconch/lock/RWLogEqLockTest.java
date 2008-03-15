package jconch.lock;

import static org.testng.AssertJUnit.*;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;

import org.testng.annotations.Test;

/**
 * Tests for {@link RWLogEqLock}.
 * 
 * @author Robert Fischer
 */
public class RWLogEqLockTest extends AbstractLogEqLockTest<ReadWriteLock, RWLogEqLock<Long>> {

    @Override
    protected RWLogEqLock<Long> createTestInstance() {
        return new RWLogEqLock<Long>();
    }

    @Test
    public void testGetGlobalInstance() {
        final int iterations = 1000;
        final int maxWaitPerThread = 20;
        final RWLogEqLock<Object> global = RWLogEqLock.getGlobalInstance();
        assertNotNull(global);

        // Now check to see if other threads ever see something different
        final AtomicBoolean sawSomethingDifferent = new AtomicBoolean(false);
        for (int i = 0; i < iterations; i++) {
            final int waitBeforeRetrieve = new Random().nextInt(maxWaitPerThread) + 1;
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
                    final RWLogEqLock<Object> myGlobal = RWLogEqLock.getGlobalInstance();
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
        } catch (final InterruptedException ie) {
            Thread.yield();
        }
        assertFalse("Saw something different", sawSomethingDifferent.get());
    }
}
