package eg.jconch.cachemap;

import jconch.cache.CacheMap;
import jconch.functor.Transformer5;

import org.apache.commons.lang.StringUtils;

public class NeedsToDoValidation {
    /**
     * This is a stupid example (these validations are fast enough), but you get
     * the gist. Imagine having to do data integrity checks, database queries,
     * LDAP look-ups, factor big integers, whatever.
     */
    private static final CacheMap<String, Boolean> validationCache = new CacheMap<String, Boolean>(
            new Transformer5<String, Boolean>() {
                public Boolean transform(final String stringToValidate) {
                    return !StringUtils.isBlank(stringToValidate) && !StringUtils.isAlpha(stringToValidate);
                }
            });

    public static boolean validateString(final String toValidate) {
        return validationCache.get(toValidate);
    }
}
