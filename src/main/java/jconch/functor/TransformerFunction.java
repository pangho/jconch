package jconch.functor;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.NullArgumentException;

import com.google.common.base.*;

public class TransformerFunction implements Function<Object,Object> {

    private final Transformer base;

    public TransformerFunction(final Transformer converter) {
        if (converter == null) {
            throw new NullArgumentException("converter");
        }
        base = converter;
    }

    public Object apply(final Object arg) {
      return base.transform(arg);
    }
}
