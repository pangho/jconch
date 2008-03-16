package test.utils;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A utility class for system memory.
 */
public final class MemoryTestUtils {

    private MemoryTestUtils() {
        // NO INSTANTIATION!
    }

    /**
     * Forces a garbage collection. There's no guarantied way to do this:
     * {@link System#gc()} is just a recommendation. But this method forces at
     * least first generation objects to be collected, and tries hard to collect
     * more than that, too.
     */
    public static final void forceGC() {
        for (int times = 0; times < 10; times++) {
            // Set up what we're looking for
            final ReferenceQueue<Object> q = new ReferenceQueue<Object>();
            final Reference ref = new SoftReference<Object>(new Object(), q);

            // Force a GC
            boolean sawGC = false;
            final List<Object> list = new ArrayList<Object>();
            list.add(ref);
            for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i++) {
                System.runFinalization();
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
        }
        return;
    }
}
