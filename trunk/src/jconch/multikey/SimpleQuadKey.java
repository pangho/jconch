package jconch.multikey;

/**
 * @author Robert Fischer, <a href="http://www.smokejumperit.com">Smokejumper Consulting</a>
 */
public class SimpleQuadKey<KEY_T> extends QuadKey<KEY_T, KEY_T, KEY_T, KEY_T> {
    public SimpleQuadKey(KEY_T key1, KEY_T key2, KEY_T key3, KEY_T key4) {
        super(key1, key2, key3, key4);
    }
}
