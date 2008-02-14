package eg.jconch.cachemap;

import java.net.InetAddress;
import java.net.UnknownHostException;

import jconch.cache.CacheMap;

import org.apache.commons.collections.Transformer;

/**
 * A class which caches reverse DNS look-ups. Rumor has it that Java does this
 * for you, and if they don't, your OS probably does. So this is probably crazy
 * redundant, but it's still a nifty example.
 */
public class NeedsADnsLookup {

    private static final CacheMap<String, String> reverseDnsCache = new CacheMap<String, String>(new Transformer() {
        public Object transform(Object dottedQuadObj) {
            if (dottedQuadObj == null) {
                return null;
            }
            try {
                return InetAddress.getByName(dottedQuadObj.toString());
            } catch (UnknownHostException e) {
                return null;
            }
        }
    });

    public static String lookupHostName(final String dottedQuad) {
        return reverseDnsCache.get(dottedQuad);
    }
}
