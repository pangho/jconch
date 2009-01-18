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

	boolean wasCalled() {
		return wasCalled;
	}
}
