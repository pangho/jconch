package jconch.cache;

import groovy.lang.Closure;
import jconch.functor.GroovyClosureTransformer;
import jconch.lock.SyncLogEqLock;

/**
 * Creates {@link CacheMap} based on a <a
 * href="http://groovy.codehaus.org/Closures">Groovy closure</a>.
 * 
 * @author Robert
 */
public class GroovyCacheMap extends ObjectCacheMap {

    public GroovyCacheMap(final Closure converter, final SyncLogEqLock<Object> lockFactory) {
        super(new GroovyClosureTransformer(converter), lockFactory);
    }

    public GroovyCacheMap(final Closure converter) {
        super(new GroovyClosureTransformer(converter));
    }

}
