package jconch.cache;

import jconch.functor.*;
import jconch.lock.SyncLogEqLock;

import org.apache.commons.collections.Transformer;

import com.google.common.base.Function;

public class ObjectCacheMap extends CacheMap<Object, Object> {

    @Deprecated
    public ObjectCacheMap(final Transformer converter, final SyncLogEqLock<Object> lockFactory) {
      super(new TransformerFunction(converter), lockFactory);
    }

    @Deprecated
    public ObjectCacheMap(final Transformer converter) {
      super(new TransformerFunction(converter));
    }

    @Deprecated
    public ObjectCacheMap(final Transformer5<Object, Object> converter, final SyncLogEqLock<Object> lockFactory) {
      this(new Transformer5Function(converter), lockFactory);
    }

    @Deprecated
    public ObjectCacheMap(final Transformer5<Object, Object> converter) {
      this(new Transformer5Function(converter));
    }

    public ObjectCacheMap(final Function<Object, Object> converter) {
      super(converter);
    }

    public ObjectCacheMap(final Function<Object, Object> converter, final SyncLogEqLock<Object> lockFactory) {
      super(converter, lockFactory);
    }

}
