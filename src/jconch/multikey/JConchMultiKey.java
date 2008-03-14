package jconch.multikey;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.util.List;

/**
 * The JConch
 */
public abstract class JConchMultiKey {

    private static final int BASE_HASH_CODE = RandomUtils.nextInt();

    public abstract int getKeyCount();

    public abstract List<Object> getKeys();

    @Override
    public int hashCode() {
        int hashCode = BASE_HASH_CODE;
        for (final Object key : getKeys()) {
            if (key == null) {
                hashCode = ~hashCode + 1;
            } else {
                hashCode = hashCode ^ key.hashCode();
            }
        }
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof JConchMultiKey) {
            final JConchMultiKey them = (JConchMultiKey) o;
            return them.getKeyCount() == this.getKeyCount() && CollectionUtils.isEqualCollection(them.getKeys(), this.getKeys());
        } else {
            return false;
        }
    }
}
