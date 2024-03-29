package jconch.functor;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.NullArgumentException;

@Deprecated
public class Transformer5Transformer extends Transformer5<Object, Object> implements Transformer {

    private final Transformer base;

    public Transformer5Transformer(final Transformer converter) {
        if (converter == null) {
            throw new NullArgumentException("converter");
        }
        base = converter;
    }

    public Object transform(final Object type) {
        return base.transform(type);
    }
}
