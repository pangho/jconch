package com.fischerventure.jconch.lock;

import static org.junit.Assert.fail;

import org.junit.Test;

public class SyncLogEqLockTest extends AbstractLogEqLockTest<SyncLogEqLock> {

	@Test
	public void testGetGlobalInstance() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLock() {
		fail("Not yet implemented");
	}

	@Override
	protected SyncLogEqLock createTestInstance() {
		return new SyncLogEqLock();
	}

}
