package jconch.cache;

import jconch.lock.SyncLogEqLock;
import jconch.test.FrameworkTest;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NOPTransformer;
import org.apache.commons.lang.NullArgumentException;
import static org.testng.AssertJUnit.*;
import org.testng.annotations.Test;

import java.util.Map;

public class ObjectCacheMapTest extends FrameworkTest {

    @Test
    public void acceptsNullOnContainsKey() {
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        map.get(null);
        assertTrue("Does not contain null", map.containsKey(null));
    }

    @Test
    public void acceptsNullOnGet() {
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        map.get(null);
    }

    @Test
    public void asTransformerChangesMapState() {
        final Object obj = new Object();
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        map.asTransformer().transform(obj);
        assertTrue("Does not contain the key", map.containsKey(obj));
    }

    @Test
    public void checkOnEquals() {
        final ObjectCacheMap map1 = new ObjectCacheMap(
                NOPTransformer.getInstance());
        final ObjectCacheMap map2 = new ObjectCacheMap(
                NOPTransformer.getInstance());
        assertTrue(map1.equals(map1));
        assertFalse(map1.equals(null));
        assertTrue(map1.equals(map2));
        assertFalse(map1.equals(new Object()));
    }

    @Test
    public void checkOnHashCode() {
        final ObjectCacheMap map1 = new ObjectCacheMap(
                NOPTransformer.INSTANCE);
        final ObjectCacheMap map2 = new ObjectCacheMap(
                NOPTransformer.INSTANCE);
        assertEquals(map1.hashCode(), map2.hashCode());
    }

    @Test
    public void clearClearsTheList() {
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        final Integer key = new Integer(1);
        map.get(key);
        assertTrue("Did not contain element", map.containsKey(key));
        map.clear();
        assertFalse("Contained element after clear", map.containsKey(key));
    }

    @Test
    public void containsKeyReturnsFalseOnWrongClass() {
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        assertFalse(map.containsKey(new Object()));
    }

    @Test(expectedExceptions = NullArgumentException.class)
    public void doubleConstructorExplodesOnFirstNull() {
        new ObjectCacheMap((Transformer) null, new SyncLogEqLock<Object>());
    }

    @Test(expectedExceptions = NullArgumentException.class)
    public void doubleConstructorExplodesOnSecondNull() {
        new ObjectCacheMap(NOPTransformer.getInstance(), null);
    }

    @Test
    public void getKeyTwice() {
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        final Object out1 = map.get(new Integer(1));
        final Object out2 = map.get(new Integer(1));
        assertEquals(out1, out2);
    }

    @Test
    public void getTransformerGetsTheSameTransformer() {
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        assertSame(NOPTransformer.getInstance(), map.getTransformer());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void putAllExplodesOnNullValue() {
        final Map in = null;
        new ObjectCacheMap(NOPTransformer.getInstance()).putAll(in);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void putExplodesOnNullValue() {
        final ObjectCacheMap map = new ObjectCacheMap(
                NOPTransformer.getInstance());
        final Integer key = new Integer(1);
        map.put(key, null);
    }

    @Test(expectedExceptions = NullArgumentException.class)
    public void singleConstructorExplodesOnNullArg() {
        new ObjectCacheMap((Transformer) null);
    }

}
