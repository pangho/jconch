package jconch.cache;

import static org.apache.commons.collections.MapUtils.synchronizedMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import jconch.lock.SyncLogEqLock;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.math.RandomUtils;

/**
 * <p>
 * A map that provides cached look-ups in a thread-safe manner. Specifically, it
 * is a thread-safe lazy map built on a memory-sensitive cache: objects are
 * created on demand through calls to {@link #get(Object)} and the like based on
 * an implementation which is passed in. The map can conceptually be treated as
 * a wrapper around {@link Transformer} or a {@link Map} of all possible inputs
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
public class CacheMap<KEY_T, VAL_T> implements Map<KEY_T, VAL_T> {

	/**
	 * Generates the initial value for the hash code builder.
	 */
	private static final int HASH_CODE_INIT_VAL = RandomUtils
			.nextInt(Integer.MAX_VALUE / 2 - 1) * 2 + 1;

	/**
	 * Generates the multiplier value for the hash code builder.
	 */
	private static final int HASH_CODE_MULT_VAL = RandomUtils
			.nextInt(Integer.MAX_VALUE / 2 - 1) * 2 + 1;

	/**
	 * The map that provides the underlying data.
	 */
	private final Map<KEY_T, VAL_T> base = synchronizedMap(new WeakHashMap<KEY_T, VAL_T>());

	/**
	 * The object that implements the locking for this object.
	 */
	private final SyncLogEqLock<KEY_T> locker;

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
	public CacheMap(final Transformer converter,
			final SyncLogEqLock<KEY_T> lockFactory) {
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
		final Object lock;
		try {
			lock = this.locker.getLock((KEY_T) key);
		} catch (ClassCastException cce) {
			return false;
		}

		synchronized (lock) {
			return this.base.containsKey(key);
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
	 * Gets the value for the given object.
	 * 
	 * @param objKey
	 *            The key to look up. May be <code>null</code>.
	 * @return The return value of the transformation.
	 * @throws ClassCastException
	 *             If the object is not of {@link KEY_T}, or the underlying
	 *             look-up returns a type that is not {@link VAL_T}.
	 */
	public VAL_T get(final Object objKey) {
		// Cast the object -- throws ClassCastException, as required
		final KEY_T key = (KEY_T) objKey;

		// Get the lock for this key
		final VAL_T out;
		synchronized (this.locker.getLock(key)) {
			// Try to get a lock
			final VAL_T attemptedFetch = this.base.get(key);
			if (attemptedFetch != null) {
				// Retrieved a cached value
				out = attemptedFetch;
			} else {
				// Okay, doesn't look like we have anything
				final VAL_T value = (VAL_T) this.converter.transform(key);
				this.base.put(key, value);
				out = value;
			}
		}
		return out;
	}

	/**
	 * Determines if there is anything in the cache.
	 * 
	 * @return If the cache is empty.
	 */
	public boolean isEmpty() {
		return this.base.isEmpty();
	}

	/**
	 * The set of keys currently loaded into the cache.
	 */
	public Set<KEY_T> keySet() {
		return this.base.keySet();
	}

	/**
	 * Sets <code>key</code> to map to <code>value</code> in this map. It
	 * will remain in the map as long as <code>key</code> is not garbage
	 * collected.
	 * 
	 * @param key
	 *            The key with which the specified value is to be associated.
	 * @param value
	 *            The value to be associated with the specified key; may not be
	 *            <code>null</code>.
	 * @throws NullPointerException
	 *             If <code>value</code> is <code>null</code>.
	 */
	public VAL_T put(final KEY_T key, final VAL_T value) {
		if (value == null) {
			throw new NullPointerException("Cannot act on null value");
		}
		synchronized (this.locker.getLock(key)) {
			return this.base.put(key, value);
		}
	}

	/**
	 * Copies all of the giving mappings into the cache. For each entry in the
	 * provided map, the general contract from {@link #put(Object, Object)}
	 * holds true.
	 * 
	 * @param t
	 *            The mapping to inject into the cache
	 * @throws NullPointerException
	 *             If <code>t</code> is <code>null</code>, or a value in
	 *             <code>t</code> is <code>null</code>.
	 */
	public void putAll(final Map<? extends KEY_T, ? extends VAL_T> t) {
		if (t == null) {
			throw new NullPointerException("Cannot act on null map");
		}
		for (final Map.Entry<? extends KEY_T, ? extends VAL_T> me : t
				.entrySet()) {
			synchronized (this.locker.getLock(me.getKey())) {
				this.put(me.getKey(), me.getValue());
			}
		}
	}

	/**
	 * Removes an object from the cache. It may be recreated at a later time.
	 */
	public VAL_T remove(final Object objKey) {
		final KEY_T key = (KEY_T) objKey;
		synchronized (this.locker.getLock(key)) {
			return this.base.remove(key);
		}
	}

	/**
	 * Provides the size of the cache at the moment.
	 */
	public int size() {
		return this.base.size();
	}

	/**
	 * Provides the values which have been generated at the moment.
	 */
	public Collection<VAL_T> values() {
		return this.base.values();
	}

	/**
	 * Provides the hash code based on the cached operation definition. See the
	 * warning in the class documentation.
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder(HASH_CODE_INIT_VAL, HASH_CODE_MULT_VAL)
				.append(this.converter).hashCode();
	}

	/**
	 * Determines equality based on cached operation definition. See the warning
	 * in the class documentation.
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		} else if (obj == this) {
			return true;
		} else if (obj instanceof CacheMap) {
			return this.converter.equals(((CacheMap) obj).converter);
		} else {
			return false;
		}
	}

	/**
	 * Provides access to this object as a {@link Transformer}. The transformer
	 * delegates to the {@link #get(Object)} method call, which (in turn)
	 * delegates to the cache operation.
	 * 
	 * @return The {@link #get(Object)} implementation as its own object.
	 */
	public Transformer asTransformer() {
		return new Transformer() {
			public Object transform(final Object in) {
				return get(in);
			}
		};
	}

	/**
	 * Provides the underlying cache operation.
	 * 
	 * @return The cache operation.
	 */
	public Transformer getTransformer() {
		return this.converter;
	}
}
