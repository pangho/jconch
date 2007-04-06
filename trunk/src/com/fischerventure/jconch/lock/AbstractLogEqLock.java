package com.fischerventure.jconch.lock;

import static org.apache.commons.collections.map.AbstractReferenceMap.*;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.map.ReferenceIdentityMap;

/**
 * Provides the basic implementation for logically equivalent locks.
 * 
 * @author Robert Fischer
 * 
 * @param <OBJ_T>
 *            The type of object which will be used for logically equivalent
 *            comparisons.
 * @param <LOCK_T>
 *            The type of lock provided by the implementing class.
 */
abstract class AbstractLogEqLock<OBJ_T, LOCK_T> {

    /**
     * The object used to represent <code>null</code>.
     */
    private final LOCK_T nullLock = createNewLock();

    /**
     * Holds onto references of objects, so that we don't lose the keys we're
     * using in the other map.
     */
    private final Map<OBJ_T, OBJ_T> holder = MapUtils.synchronizedMap(new ReferenceIdentityMap(WEAK, HARD));

    /**
     * The object providing locks.
     */
    private final Map<OBJ_T, KeyAndLock> locks = MapUtils.synchronizedMap(MapUtils.lazyMap(
            new WeakHashMap<OBJ_T, KeyAndLock>(), new KeyAndLockTransformer()));

    /**
     * Provides a lock for the given object.
     * 
     * @param in
     *            The object whose lock is wanted.
     * @return A lock for that object.
     */
    public synchronized LOCK_T getLock(final OBJ_T in) {
        // Handle this case here, so we can assume that "in" is not null from
        // here on out.
        if (in == null) {
            return nullLock;
        }

        // Get the canonical version of the logically equivalent objects. Note
        // that the KeyAndLock holds a WeakReference, and the "get" is logically
        // equivalent, so it's possible for the key to be GCed. If so, we need
        // to repeat the process. Sooner or later the key will become the
        // parameter to this method, which means it won't be GCed and we can
        // terminate this algorithm.
        OBJ_T key = null;
        KeyAndLock keyAndLock = null;
        while (key == null) {
            keyAndLock = locks.get(in);
            key = keyAndLock.getKey();
        }

        // Now that we have a hard reference to the canonical key, we can put it
        // into the refence-based holder to keep the canonical key from being
        // GC'ed before the argument. Note that we don't want to do this if they
        // are referentially the same argument, or we end up with a memory leak.
        if (in != key) {
            holder.put(in, key);
        }

        // Now we can return the lock we retrieved before
        return keyAndLock.getLock();
    }

    /**
     * Determines if we have a lock for the given object currently at hand.
     * 
     * @param obj
     *            The object to check on.
     * @return If {@link #getLock(Object)} applied to the parameter would
     *         provide a new lock.
     */
    protected synchronized boolean hasLockFor(final OBJ_T obj) {
        return this.holder.containsKey(obj);
    }

    /**
     * Implementation of the "lock" that is created.
     * 
     * @return An object to use for a lock.
     */
    protected abstract LOCK_T createNewLock();

    /**
     * Provides both a key and its associated lock.
     * 
     * @author rfischer
     * @version $Date: Apr 6, 2007 8:29:44 AM $
     */
    private final class KeyAndLock {

        private final Reference keyRef;

        private final LOCK_T lock;

        public KeyAndLock(final OBJ_T key) {
            keyRef = new SoftReference<OBJ_T>(key);
            lock = createNewLock();
        }

        public OBJ_T getKey() {
            return (OBJ_T) keyRef.get();
        }

        public LOCK_T getLock() {
            return lock;
        }
    }

    /**
     * A transformer that takes the argument and generates a {@link KeyAndLock}
     * from it.
     * 
     * @author rfischer
     * @version $Date: Apr 6, 2007 8:34:26 AM $
     */
    private final class KeyAndLockTransformer implements Transformer {

        /**
         * Casts the argument into {@link OBJ_T} and then uses it to create a
         * KeyAndLock for this instance.
         * 
         * @param key
         *            The key to apply.
         * @return The {@link KeyAndLock}
         */
        public KeyAndLock transform(final Object key) {
            return new KeyAndLock((OBJ_T) key);
        }

    }
}
