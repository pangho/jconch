package jconch.lock;

import static org.testng.AssertJUnit.*;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import org.testng.annotations.Test;

public class SyncLogEqLockTest extends AbstractLogEqLockTest<Object, SyncLogEqLock<Long>> {

    @Override
    protected SyncLogEqLock<Long> createTestInstance() {
        return new SyncLogEqLock<Long>();
    }

    @Test
    public void testGetGlobalInstance() {
        final int iterations = 1000;
        final int maxWaitPerThread = 20;
        final SyncLogEqLock<Object> global = SyncLogEqLock.getGlobalInstance();
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
                    final SyncLogEqLock<Object> myGlobal = SyncLogEqLock.getGlobalInstance();
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
