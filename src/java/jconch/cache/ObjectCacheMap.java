package jconch.cache;

import jconch.functor.Transformer5;
import jconch.functor.Transformer5Transformer;
import jconch.lock.SyncLogEqLock;

import org.apache.commons.collections.Transformer;

public class ObjectCacheMap extends CacheMap<Object, Object> {

    public ObjectCacheMap(final Transformer converter, final SyncLogEqLock<Object> lockFactory) {
        super(new Transformer5Transformer(converter), lockFactory);
    }

    public ObjectCacheMap(final Transformer converter) {
        super(new Transformer5Transformer(converter));
    }

    public ObjectCacheMap(final Transformer5<Object, Object> converter, final SyncLogEqLock<Object> lockFactory) {
        super(converter, lockFactory);
    }

    public ObjectCacheMap(final Transformer5<Object, Object> converter) {
        super(converter);
    }

}
