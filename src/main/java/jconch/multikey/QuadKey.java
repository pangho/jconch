package jconch.multikey;

import java.util.Arrays;
import java.util.List;

/**
 * @author Robert Fischer, <a href="http://www.smokejumperit.com">Smokejumper Consulting</a>
 */
public class QuadKey<KEY_T1, KEY_T2, KEY_T3, KEY_T4> extends TriKey<KEY_T1, KEY_T2, KEY_T3> {
    private final KEY_T4 key4;

    public QuadKey(KEY_T1 key1, KEY_T2 key2, KEY_T3 key3, KEY_T4 key4) {
        super(key1, key2, key3);
        this.key4 = key4;
    }

    @Override
    public List<Object> getKeys() {
        return Arrays.asList(getKey1(), getKey2(), getKey3(), getKey4());
    }

    @Override
    public int getKeyCount() {
        return 4;
    }

    public KEY_T4 getKey4() {
        return key4;
    }
}
