package jconch.cache;

import groovy.lang.Closure;
import jconch.functor.GroovyClosureTransformer;
import jconch.lock.SyncLogEqLock;

public class GroovyCacheMap extends ObjectCacheMap {

    public GroovyCacheMap(final Closure converter, final SyncLogEqLock<Object> lockFactory) {
        super(new GroovyClosureTransformer(converter), lockFactory);
    }

    public GroovyCacheMap(final Closure converter) {
        super(new GroovyClosureTransformer(converter));
    }

}
