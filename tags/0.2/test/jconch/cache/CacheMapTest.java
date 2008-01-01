package jconch.cache;

import static org.testng.AssertJUnit.*;

import java.util.Map;

import jconch.lock.SyncLogEqLock;
import jconch.test.FrameworkTest;

import org.apache.commons.collections.functors.NOPTransformer;
import org.apache.commons.lang.NullArgumentException;
import org.testng.annotations.Test;

public class CacheMapTest extends FrameworkTest {

	@Test
	public void acceptsNullOnContainsKey() {
		final CacheMap<Object, Object> map = new CacheMap<Object, Object>(
				NOPTransformer.getInstance());
		map.get(null);
		assertTrue("Does not contain null", map.containsKey(null));
	}

	@Test
	public void acceptsNullOnGet() {
		final CacheMap<Object, Object> map = new CacheMap<Object, Object>(
				NOPTransformer.getInstance());
		map.get(null);
	}

	@Test
	public void asTransformerChangesMapState() {
		final Object obj = new Object();
		final CacheMap<Object, Object> map = new CacheMap<Object, Object>(
				NOPTransformer.getInstance());
		map.asTransformer().transform(obj);
		assertTrue("Does not contain the key", map.containsKey(obj));
	}

	@Test
	public void checkOnEquals() {
		final CacheMap<Integer, Object> map1 = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		final CacheMap<Integer, Object> map2 = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		assertTrue(map1.equals(map1));
		assertFalse(map1.equals(null));
		assertTrue(map1.equals(map2));
		assertFalse(map1.equals(new Object()));
	}

	@Test
	public void checkOnHashCode() {
		final CacheMap<Integer, Object> map1 = new CacheMap<Integer, Object>(
				NOPTransformer.INSTANCE);
		final CacheMap<Integer, Object> map2 = new CacheMap<Integer, Object>(
				NOPTransformer.INSTANCE);
		assertEquals(map1.hashCode(), map2.hashCode());
	}

	@Test
	public void clearClearsTheList() {
		final CacheMap<Integer, Object> map = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		final Integer key = new Integer(1);
		map.get(key);
		assertTrue("Did not contain element", map.containsKey(key));
		map.clear();
		assertFalse("Contained element after clear", map.containsKey(key));
	}

	@Test
	public void containsKeyReturnsFalseOnWrongClass() {
		final CacheMap<Integer, Object> map = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		assertFalse(map.containsKey(new Object()));
	}

	@Test(expectedExceptions = NullArgumentException.class)
	public void doubleConstructorExplodesOnFirstNull() {
		new CacheMap<Object, Object>(null, new SyncLogEqLock<Object>());
	}

	@Test(expectedExceptions = NullArgumentException.class)
	public void doubleConstructorExplodesOnSecondNull() {
		new CacheMap<Object, Object>(NOPTransformer.getInstance(), null);
	}

	@Test
	public void getKeyTwice() {
		final CacheMap<Integer, Object> map = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		final Object out1 = map.get(new Integer(1));
		final Object out2 = map.get(new Integer(1));
		assertEquals(out1, out2);
	}

	@Test
	public void getTransformerGetsTheSameTransformer() {
		final CacheMap<Integer, Object> map = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		assertSame(NOPTransformer.getInstance(), map.getTransformer());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void putAllExplodesOnNullValue() {
		final Map<Integer, Object> in = null;
		new CacheMap<Integer, Object>(NOPTransformer.getInstance()).putAll(in);
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void putExplodesOnNullValue() {
		final CacheMap<Integer, Object> map = new CacheMap<Integer, Object>(
				NOPTransformer.getInstance());
		final Integer key = new Integer(1);
		map.put(key, null);
	}

	@Test(expectedExceptions = NullArgumentException.class)
	public void singleConstructorExplodesOnNullArg() {
		new CacheMap<Object, Object>(null);
	}

}