package jconch.testing;

import java.util.concurrent.Callable;

/**
 * A dummy Callable capable of telling you if it was called or not.
 *
 * @author Hamlet D'Arcy
 */
class DummyCallable implements Callable<Void> {
	private boolean wasCalled = false;

	public Void call() {
		wasCalled = true;
		return null;
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
