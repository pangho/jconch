package jconch.functor;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.NullArgumentException;

import com.google.common.base.*;

public class Transformer5Function<IN_T,OUT_T> implements Function<IN_T,OUT_T> {

    private final Transformer5<IN_T,OUT_T> base;

    public Transformer5Function(final Transformer5<IN_T,OUT_T> converter) {
        if (converter == null) {
            throw new NullArgumentException("converter");
        }
        base = converter;
    }

    public OUT_T apply(final IN_T arg) {
      return base.transform(arg);
    }
}
