package eg.jconch.cachemap;

import jconch.cache.CacheMap;
import jconch.functor.Transformer5;
import jconch.multikey.TriKey;

/**
 * Example of using a typed multikey to wrap the handling of a method.
 */
public class UsingTypedMultiKey {

    /**
     * This is the super-secret implementation method.  It probably used to be the API method.
     */
    private String arbitraryMethodImpl(final String str, final int i, final double d) {
        return str + (i + d);
    }

    /**
     * This is the public API method.
     */
    public String arbitraryMethod(final String str, final int i, final double d) {
        return arbitraryCache.get(new TriKey(str, i, d));
    }

    /**
     * This is a class which is created mainly to make the typing a bit saner.  You could also, I suppose,
     * add convenience methods liked named setters (which would let you pretend you have named arguments!)
     */
    private static final class ArbitraryMethodKey extends TriKey<String, Integer, Double> {
        public ArbitraryMethodKey(String key1, Integer key2, Double key3) {
            super(key1, key2, key3);
        }
    }

    /**
     * And the CacheMap that ties it all together.
     */
    private final CacheMap<ArbitraryMethodKey, String> arbitraryCache = new CacheMap<ArbitraryMethodKey, String>(
            new Transformer5<ArbitraryMethodKey, String>() {
                public String transform(final ArbitraryMethodKey multiKey) {
                    return arbitraryMethodImpl(multiKey.getKey1(), multiKey.getKey2(), multiKey.getKey3());
                }
            });

}
