package jconch.cache;

import static java.util.Collections.synchronizedMap;
import static org.apache.commons.collections.CollectionUtils.transformedCollection;
import static org.apache.commons.collections.SetUtils.transformedSet;

import java.util.*;

import jconch.lock.SyncLogEqLock;

import org.apache.commons.collections.keyvalue.TiedMapEntry;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.math.RandomUtils;

import com.google.common.collect.MapMaker;
import com.google.common.base.Function;
import com.google.common.collect.ForwardingMap;

/**
 * <p>
 * A map that provides cached look-ups in a thread-safe manner. Specifically, it
 * is a thread-safe lazy map built on a memory-sensitive cache: objects are
 * created on demand through calls to {@link #get(Object)} and the like based on
 * an implementation which is passed in. The map can conceptually be treated as
 * a wrapper around {@link Function} or a {@link Map} of all possible inputs
 * onto all possible outputs.
 * </p>
 * <p>
 * <b>WARNING:</b> This Map violates the general Map contract in a few ways.
 * <br>
 * First, its behavior is similar to a {@link WeakHashMap}, in that the methods
 * may behave as if a seperate thread is silently removing entries. Because of
 * this, it is not guarantied that calls to {@link #put(Object, Object)} and the
 * like will be honored beyond the life of the "put-ed" object. All of the
 * methods reflect the state of the cache at the moment the method is called,
 * and they cannot be guarantied to be consistant, even in a single threaded
 * environment. <br>
 * Second, the {@link #hashCode()} and {@link #equals(Object)} methods are based
 * off of the definition of the cache operation, not the current contents. This
 * prevents those methods from being sensitive to the state of the cache, but
 * also deviates from {@link Map#equals(Object)} and {@link Map#hashCode()}.
 * </p>
 * 
 * @author Robert Fischer
 * 
 * @param <KEY_T>
 *            The type of the keys in the map, and the arguments for the
 *            transformer.
 * @param <VAL_T>
 *            The type of the values in the map, and the return values from the
 *            transformer.
 */
public class CacheMap<KEY_T, VAL_T> extends ForwardingMap<KEY_T, VAL_T> implements Map<KEY_T, VAL_T> {

    private final Map<KEY_T,VAL_T> delegateMap;

    /**
     * Constructs a new instance of the cache map, which uses its own internal
     * set of locks (see {@link CacheMap#CacheMap(Function, SyncLogEqLock)}).
     * 
     * @param converter
     *            The transformer that implements the caching behavior.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>.
     */
    public CacheMap(final Function<KEY_T, VAL_T> converter) {
        this(converter, new SyncLogEqLock<KEY_T>());
    }

    /**
     * Constructs a new instance of the cache map.
     * 
     * @param converter
     *            The transformer that implements the caching behavior.
     * @param lockFactory
     *            The source to be used for locking behaviors.
     * @throws NullArgumentException
     *             If either argument is <code>null</code>.
     */
    public CacheMap(final Function<KEY_T, VAL_T> converter, final SyncLogEqLock<KEY_T> lockFactory) {
        if (converter == null) {
            throw new NullArgumentException("converter");
        }
        if (lockFactory == null) {
            throw new NullArgumentException("lockFactory");
        }

        delegateMap = new MapMaker()
          .softKeys()
          .makeComputingMap(new Function<KEY_T,VAL_T>() { 
            public VAL_T apply(KEY_T key) {
              synchronized(lockFactory.getLock(key)) {
                return converter.apply(key);
              }
            }
          })
        ;
    }

    protected Map<KEY_T,VAL_T> delegate() { return delegateMap; }

}
