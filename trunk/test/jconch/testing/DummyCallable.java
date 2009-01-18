package jconch.testing;

import java.util.concurrent.Callable;

/**
 * A dummy Callable capable of telling you if it was called or not.
 *
 * @author Hamlet D'Arcy
 */
class DummyCallable implements Callable<Void> {
	private boolean wasCalled = false;

	public Void call() throws Exception {
		wasCalled = true;
		return null;
	}

	boolean wasCalled() {
		return wasCalled;
	}
}
