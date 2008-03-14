package jconch.multikey;

import java.util.Arrays;
import java.util.List;

/**
 * @author Robert Fischer, <a href="http://www.smokejumperit.com">Smokejumper Consulting</a>
 */
public class TriKey<KEY_T1, KEY_T2, KEY_T3> extends DuoKey<KEY_T1, KEY_T2> {
    private final KEY_T3 key3;

    public TriKey(KEY_T1 key1, KEY_T2 key2, KEY_T3 key3) {
        super(key1, key2);
        this.key3 = key3;
    }

    public int getKeyCount() {
        return 3;
    }

    public KEY_T3 getKey3() {
        return key3;
    }

    public List<Object> getKeys() {
        return Arrays.asList(getKey1(), getKey2(), getKey3());
    }
}
