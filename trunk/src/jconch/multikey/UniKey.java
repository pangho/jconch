package jconch.multikey;

import java.util.Arrays;
import java.util.List;

/**
 * A holder for a single key.
 */
public class UniKey<KEY_T> extends JConchMultiKey {

    private final KEY_T key;

    public UniKey(KEY_T key) {
        this.key = key;
    }

    public int getKeyCount() {
        return 1;
    }

    public KEY_T getKey1() {
        return key;
    }

    public List<Object> getKeys() {
        return (List<Object>) Arrays.asList(key);
    }

}
