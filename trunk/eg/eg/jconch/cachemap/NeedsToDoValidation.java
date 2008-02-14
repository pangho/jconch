package eg.jconch.cachemap;

import jconch.cache.CacheMap;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.StringUtils;

public class NeedsToDoValidation {
    /**
     * This is a stupid example (these validations are fast enough), but you get
     * the gist. Imagine having to do data integrity checks, database queries,
     * LDAP look-ups, factor big integers, whatever.
     */
    private static final CacheMap<String, Boolean> validationCache = new CacheMap<String, Boolean>(new Transformer() {
        public Object transform(final Object stringToValidate) {
            if (stringToValidate == null) {
                return false;
            }
            return !StringUtils.isBlank(stringToValidate.toString())
                    && !StringUtils.isAlpha(stringToValidate.toString());
        }
    });

    public static boolean validateString(final String toValidate) {
        return validationCache.get(toValidate);
    }
}
