package jconch.functor;

import groovy.lang.Closure;

import org.apache.commons.lang.NullArgumentException;

public class GroovyClosureTransformer implements Transformer5<Object, Object> {

    private final Closure impl;

    public GroovyClosureTransformer(final Closure closure) {
        if (closure == null) {
            throw new NullArgumentException("closure");
        }
        impl = closure;
    }

    public Object transform(final Object arg) {
        return impl.call(arg);
    }

}
