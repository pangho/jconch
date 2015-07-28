# The Case for CacheMap (a Thread-Safe Lazy Map) #

The purpose of a thread-safe lazy map is to cache expensive look-ups and calculations, where the cost of looking up a keyed value is so severe that it's worth spending some memory to cache commonly used values.  The most obvious example is in database look-ups, but DNS look-ups and potentially expensive data validations are other good cases.

Down near the bottom, there is even an example of using a MultiKey to cache an arbitrary method.  Synchronization in that case works thanks to the LogEqLock synchronization and MultiKey's careful implementation.  Isn't it nifty how everything works together?

Keep in mind that in all these cases, you're going to get to cache the most recent values.  Exactly how many values you get to cache depends on how much memory you have to spare and how aggressive your GC is.

These examples are checked into source code [here](http://code.google.com/p/jconch/source/browse/trunk/eg/eg/jconch/cachemap/).

## Database Entity Look-Ups ##

Many applications spend a lot of time doing look-ups of ORM data entities by key.  This is trivially done with the lazy map.
```
    private static final CacheMap<Integer, ToyEntity> entityCache = new CacheMap<Integer, ToyEntity>(
            new Transformer5<Integer, ToyEntity>() {
                public ToyEntity transform(final Integer primaryKey) {
                    return getEntityManager().find(ToyEntity.class, primaryKey);
                }
            });

    public static ToyEntity getEntity(final int primaryKey) {
        return entityCache.get(primaryKey);
    }
```

## Reverse DNS Look-Ups ##

As [noted elsewhere](http://www.oreillynet.com/onjava/blog/2005/11/reverse_dns_lookup_and_java.html), reverse DNS lookups can be really incredibly slow.  Now, I'm not sure if Java or your operating system is going to be caching them, but if not, you could do something like this:
```
    private static final CacheMap<String, String> reverseDnsCache = new CacheMap<String, String>(
            new Transformer5<String, String>() {
                public String transform(String dottedQuadObj) {
                    if (dottedQuadObj == null) {
                        return null;
                    }
                    try {
                        return InetAddress.getByName(dottedQuadObj).getHostName();
                    } catch (UnknownHostException e) {
                        return null;
                    }
                }
            });

    public static String lookupHostName(final String dottedQuad) {
        return reverseDnsCache.get(dottedQuad);
    }
```

## Expensive Validations ##

This is a stupid example (these validations are fast enough), but you get the gist.  Imagine having to do data integrity checks, database queries, LDAP look-ups, factor big integers, whatever.

```
    private static final CacheMap<String, Boolean> validationCache = new CacheMap<String, Boolean>(
            new Transformer5<String, Boolean>() {
                public Boolean transform(final String stringToValidate) {
                    return !StringUtils.isBlank(stringToValidate) && !StringUtils.isAlpha(stringToValidate);
                }
            });

    public static boolean validateString(final String toValidate) {
        return validationCache.get(toValidate);
    }
```

## MultiKey Example ##
### Option #1 ###
Just use the [MultiKey](http://commons.apache.org/collections/api-release/org/apache/commons/collections/keyvalue/MultiKey.html), and you can use the CacheMap to cache any arbitrary method.
```
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
```

### Option #2 ###
Use the [jconch.multikey](http://code.google.com/p/jconch/source/browse/trunk/src/jconch/multikey) package to get typed versions of the same code.
```
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

```

## Groovy Cache Map ##
Of course, if you're using Groovy, you can make life go a whole lot faster.
```
def cacheMap = new GroovyCacheMap() { a -> a * 2 }
(1..5).each { i ->
    println cacheMap[i]
}
```