package jconch.multikey;

import java.util.Arrays;
import java.util.List;

/**
 * @author Robert Fischer, <a href="http://www.smokejumperit.com">Smokejumper Consulting</a>
 */
public class DuoKey<KEY_T1, KEY_T2> extends UniKey<KEY_T1> {

    private final KEY_T2 key2;

    public DuoKey(KEY_T1 key1, KEY_T2 key2) {
        super(key1);
        this.key2 = key2;
    }

    public KEY_T2 getKey2() {
        return key2;
    }

    public int getKeyCount() {
        return 2;
    }

    public List<Object> getKeys() {
        return Arrays.asList(getKey1(), getKey2());
    }
}
