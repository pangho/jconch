package jconch.cache;

import static org.junit.Assert.*;
import jconch.lock.SyncLogEqLock;

import org.apache.commons.collections.functors.NOPTransformer;
import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class CacheMapTest {
	// TODO Test whether passing null to "contains value" will explode

	@Test(expected = NullArgumentException.class)
	public void singleConstructorExplodesOnNullArg() {
		new CacheMap(null);
	}

	@Test(expected = NullArgumentException.class)
	public void doubleConstructorExplodesOnFirstNull() {
		new CacheMap(null, new SyncLogEqLock());
	}

	@Test(expected = NullArgumentException.class)
	public void doubleConstructorExplodesOnSecondNull() {
		new CacheMap(NOPTransformer.getInstance(), null);
	}

	@Test
	public void asTransformerChangesMapState() {
		final Object obj = new Object();
		final CacheMap map = new CacheMap(NOPTransformer.getInstance());
		map.asTransformer().transform(obj);
		assertTrue("Does not contain the key", map.containsKey(obj));
	}

	@Test
	public void acceptsNullOnGet() {
		final CacheMap map = new CacheMap(NOPTransformer.getInstance());
		map.get(null);
	}

	@Test
	public void acceptsNullOnContainsKey() {
		final CacheMap map = new CacheMap(NOPTransformer.getInstance());
		map.get(null);
		assertTrue("Does not contain null", map.containsKey(null));
	}
}
