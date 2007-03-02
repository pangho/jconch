package com.fischerventure.jconch.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Class that provides {@link ReadWriteLock} instances tagged by logically
 * equivalent objects.
 * 
 * @author Robert Fischer
 */
public class RWLogEqLock extends AbstractLogEqLock<ReadWriteLock> {

	/**
	 * The global instance.
	 */
	private static final RWLogEqLock global = new RWLogEqLock();

	/**
	 * Provides the same instance of this class every time.
	 * 
	 * @return A singleton instance.
	 */
	public static RWLogEqLock getGlobalInstance() {
		return global;
	}

	/**
	 * Constructor.
	 */
	public RWLogEqLock() {
		// Does nothing.
	}

	@Override
	protected ReadWriteLock createNewLock() {
		return new ReentrantReadWriteLock();
	}
}
