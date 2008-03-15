package eg.jconch.cachemap;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jconch.cache.CacheMap;
import jconch.functor.Transformer5;

/**
 * A class which caches reverse DNS look-ups. Rumor has it that Java does this
 * for you, and if they don't, your OS probably does. So this is probably crazy
 * redundant, but it's still a nifty example.
 */
public class NeedsADnsLookup {

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
}
