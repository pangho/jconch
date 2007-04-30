package jconch.cache;

import static org.apache.commons.collections.MapUtils.synchronizedMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;

import jconch.lock.RWLogEqLock;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.NullArgumentException;

/**
 * A map that provides cached look-ups in a thread-safe manner.
 * 
 * @author Robert Fischer
 * 
 * @param <KEY_T>
 * @param <VAL_T>
 */
public class CacheMap<KEY_T, VAL_T> implements Map<KEY_T, VAL_T> {

	/**
	 * The map that provides the underlying data.
	 */
	private final Map<KEY_T, VAL_T> base = synchronizedMap(new WeakHashMap<KEY_T, VAL_T>());

	/**
	 * The object that implements the locking for this object.
	 */
	private final RWLogEqLock<KEY_T> locker;

	/**
	 * The object wrapping the cache behavior.
	 */
	private final Transformer converter;

	/**
	 * Constructs a new instance of the cache map.
	 * 
	 * @param converter
	 *            The transformer that implements the caching behavior.
	 * @throws NullArgumentException
	 *             If the argument is <code>null</code>.
	 */
	public CacheMap(final Transformer converter) {
		this(converter, new RWLogEqLock<KEY_T>());
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
	public CacheMap(final Transformer converter,
			final RWLogEqLock<KEY_T> lockFactory) {
		if (converter == null) {
			throw new NullArgumentException("converter");
		}
		this.converter = converter;

		if (lockFactory == null) {
			throw new NullArgumentException("lockFactory");
		}
		this.locker = lockFactory;
	}

	/**
	 * Clears the cache.
	 */
	public void clear() {
		synchronized (base) {
			base.clear();
		}
	}

	/**
	 * Determines whether the value for the current key is in cache.
	 * 
	 * @param key
	 *            The object to check, may be <code>null</code>
	 * @return Whether the object is in the cache at the moment
	 */
	public boolean containsKey(final Object key) {
		// Retrieve the read lock for this object
		final Lock readLock;
		try {
			readLock = this.locker.getLock((KEY_T) key).readLock();
		} catch (ClassCastException cce) {
			return false;
		}

		readLock.lock();
		try {
			return this.base.containsKey(key);
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Whether the value is in the cache.
	 */
	public boolean containsValue(Object value) {
		return base.containsValue(value);
	}

	/**
	 * Provides the set of cached values.
	 */
	public Set<java.util.Map.Entry<KEY_T, VAL_T>> entrySet() {
		return base.entrySet();
	}

	/**
	 * Attempts to get the
	 */
	public VAL_T get(final Object key) {
		throw new NotImplementedException();
	}

	public boolean isEmpty() {
		throw new NotImplementedException();
	}

	public Set<KEY_T> keySet() {
		throw new NotImplementedException();
	}

	public VAL_T put(KEY_T key, VAL_T value) {
		throw new NotImplementedException();
	}

	public void putAll(Map<? extends KEY_T, ? extends VAL_T> t) {
		throw new NotImplementedException();
	}

	public VAL_T remove(Object key) {
		throw new NotImplementedException();
	}

	public int size() {
		throw new NotImplementedException();
	}

	public Collection<VAL_T> values() {
		throw new NotImplementedException();
	}

}
