package jconch.multikey;

/**
 * @author Robert Fischer, <a href="http://www.smokejumperit.com">Smokejumper Consulting</a>
 */
public class SimpleTriKey<KEY_T> extends TriKey<KEY_T, KEY_T, KEY_T> {
    public SimpleTriKey(KEY_T key1, KEY_T key2, KEY_T key3) {
        super(key1, key2, key3);
    }
}
