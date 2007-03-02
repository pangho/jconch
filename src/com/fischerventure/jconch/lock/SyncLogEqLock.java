package com.fischerventure.jconch.lock;

/**
 * This class should be used to implement logically equivalent locks.
 * <p>
 * Currently, Java has a major failing in its synchronization mechanism. The
 * <code>synchronize</code> keyword operates at the reference level, which
 * means that two different but logically equivalent objects can enter the same
 * synchronized block concurrently. Since POJOs are often generated such that
 * they are logically equivalent, but referentially different, this is a
 * problem.
 * <p>
 * A single instance of this class will provide the same {@link Object} instance
 * for each logically equivalent object passed in.
 * 
 * @author Robert Fischer
 * 
 */
public class SyncLogEqLock extends AbstractLogEqLock<Object> {

	/**
	 * Default constructor.
	 */
	public SyncLogEqLock() {

	}

	/**
	 * Provides the same instance of this class every time it is called.
	 * 
	 * @return
	 */
	public static SyncLogEqLock getGlobalInstance() {
		return null;
	}

	/**
	 * @return A newly-instantiated {@link Object}.
	 */
	@Override
	protected Object createNewLock() {
		return new Object();
	}
}
