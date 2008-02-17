package jconch.cache;

import jconch.functor.Transformer5;
import jconch.lock.SyncLogEqLock;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.NullArgumentException;

public class ObjectCacheMap extends CacheMap<Object, Object> {

    public ObjectCacheMap(final Transformer converter, final SyncLogEqLock<Object> lockFactory) {
        this(new Transformer5Transformer(converter), lockFactory);
    }

    public ObjectCacheMap(final Transformer converter) {
        this(new Transformer5Transformer(converter));
    }

    public ObjectCacheMap(final Transformer5<Object, Object> converter, final SyncLogEqLock<Object> lockFactory) {
        super(converter, lockFactory);
    }

    public ObjectCacheMap(final Transformer5<Object, Object> converter) {
        super(converter);
    }

    private static final class Transformer5Transformer implements Transformer5<Object, Object> {
        private final Transformer base;

        public Transformer5Transformer(final Transformer converter) {
            if (converter == null) {
                throw new NullArgumentException("converter");
            }
            base = converter;
        }

        public Object transform(final Object type) {
            return base.transform(type);
        }
    }

}
