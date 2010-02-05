package jconch.testing;

import java.util.concurrent.Callable;

/**
 * A dummy Runnable capable of telling you if it was called or not. 
 *
 * @author Hamlet D'Arcy
 */
class DummyRunnable implements Runnable {
	private boolean wasCalled = false;

	public void run() {
		wasCalled = true;
	}

	/**
	 * Lets you know whether this Runnable was invoked.
	 * @return
	 * 		true if this was invoked, false otherwise
	 */
	boolean wasCalled() {
		return wasCalled;
	}
}
