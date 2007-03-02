package com.fischerventure.jconch.lock;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provides the basic implementation for logically equivalent locks.
 * 
 * @author Robert Fischer
 * 
 * @param <LOCK_T>
 *            The type of lock provided by the implementing class.
 */
abstract class AbstractLogEqLock<LOCK_T> {

	/**
	 * The object used to represent <code>null</code>.
	 */
	private final LOCK_T nullLock = createNewLock();

	/**
	 * The object providing locks.
	 */
	private final Map<Object, LOCK_T> locks = new WeakHashMap<Object, LOCK_T>();

	/**
	 * Provides a lock for the given object.
	 * 
	 * @param in
	 *            The object whose lock is wanted.
	 * @return A lock for that object.
	 */
	public synchronized Object getLock(final Object in) {
		// Handle this case here, so we can assume that "in" is not null from
		// here on out.
		if (in == null) {
			return nullLock;
		}

		// The WeakHashMap only clears on put. If it clears on get, this falls
		// apart. We can't use "remove" here, despite the how much we might like
		// to, because that will clear the map.
		final LOCK_T oldLock = locks.get(in);
		final LOCK_T outLock;
		if (oldLock != null) {
			// If we overwrote a lock we were using before, put the old one
			// back and return it. We need to wipe out the previous object
			// reference -- otherwise, it will be GC'ed and we'll lose the lock.
			locks.remove(in);
			outLock = oldLock;
		} else {
			// There was no old lock, so we're using the newly-generated lock.
			final LOCK_T newLock = createNewLock();
			outLock = newLock;
		}

		// Store and then return the lock of choice.
		locks.put(in, outLock);
		return outLock;
	}

	/**
	 * Determines if we have a lock for the given object currently at hand.
	 * 
	 * @param obj
	 *            The object to check on.
	 * @return If {@link #getLock(Object)} applied to the parameter would
	 *         provide a new lock.
	 */
	protected synchronized boolean hasLockFor(final Object obj) {
		return this.locks.containsKey(obj);
	}

	/**
	 * Implementation of the "lock" that is created.
	 * 
	 * @return An object to use for a lock.
	 */
	protected abstract LOCK_T createNewLock();
}
