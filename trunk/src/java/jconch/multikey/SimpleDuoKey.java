package jconch.multikey;

/**
 * Like {@link DuoKey}, but with one class.
 *
 * @author Robert Fischer, <a href="http://www.smokejumperit.com">Smokejumper Consulting</a>
 */
public class SimpleDuoKey<KEY_T> extends DuoKey<KEY_T, KEY_T> {
    public SimpleDuoKey(KEY_T key1, KEY_T key2) {
        super(key1, key2);
    }
}
