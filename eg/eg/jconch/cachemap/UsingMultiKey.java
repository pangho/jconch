package eg.jconch.cachemap;

import jconch.cache.CacheMap;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.keyvalue.MultiKey;

public class UsingMultiKey {

    private final CacheMap<MultiKey, String> arbitraryCache = new CacheMap<MultiKey, String>(new Transformer() {
        public Object transform(final Object multiKeyObj) {
            final MultiKey multiKey = (MultiKey) multiKeyObj;
            return arbitraryMethod((String) multiKey.getKey(0), (Integer) multiKey.getKey(1), (Double) multiKey
                    .getKey(2));
        }
    });

    public String arbitraryMethod(final String str, final int i, final double d) {
        return str + (i + d);
    }

    public String arbitraryMethodCached(final String str, final int i, final double d) {
        return arbitraryCache.get(new MultiKey(str, i, d));
    }
}
