package jconch.cache;

import static org.junit.Assert.*;
import jconch.lock.SyncLogEqLock;
import jconch.test.FrameworkTest;

import org.apache.commons.collections.functors.NOPTransformer;
import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class CacheMapTest extends FrameworkTest {
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

	@Test
	public void containsKeyReturnsFalseOnWrongClass() {
		final CacheMap<Integer, Object> map = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		assertFalse(map.containsKey(new Object()));
	}

	@Test
	public void getKeyTwice() {
		final CacheMap<Integer, Object> map = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		final Object out1 = map.get(new Integer(1));
		final Object out2 = map.get(new Integer(1));
		assertEquals(out1, out2);
	}
}
