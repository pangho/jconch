package jconch.multikey;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.util.List;

/**
 * A fast, typed, non-modifiable, thread-safe implementation of having multiple keys for a map.
 */
public abstract class JConchMultiKey {

    /**
     * The number of elements in the multikey.
     *
     * @return The cardinality of this implementation.
     */
    public abstract int getKeyCount();

    /**
     * The keys that make up the multikey.
     *
     * @return An unmodifiable list of keys that make up the map.
     */
    public abstract List<Object> getKeys();

    /**
     * An arbitrary value from which to start our hash code algorithm.
     */
    private static final int BASE_HASH_CODE = RandomUtils.nextInt();

    /**
     * The cached hash code for this map.  Cached so that we get thread safety and speed, and because it's
     * almost inevitably going to be used in any case.
     */
    private final int hashCode;

    public JConchMultiKey() {
        int myHashCode = BASE_HASH_CODE;
        for (final Object key : getKeys()) {
            if (key == null) {
                myHashCode = ~myHashCode + 1;
            } else {
                myHashCode = myHashCode ^ key.hashCode();
            }
        }
        hashCode = myHashCode;
    }


    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof JConchMultiKey) {
            final JConchMultiKey them = (JConchMultiKey) o;
            return them.getKeyCount() == this.getKeyCount() && them.hashCode() == this.hashCode() && CollectionUtils.isEqualCollection(them.getKeys(), this.getKeys());
        } else {
            return false;
        }
    }
}
