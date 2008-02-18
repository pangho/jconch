package jconch.functor;

import groovy.lang.Closure;

public class GroovyClosureTransformer implements Transformer5<Object, Object> {

    private final Closure impl;

    public GroovyClosureTransformer(final Closure closure) {
        impl = closure;
    }

    public Object transform(final Object arg) {
        return impl.call(arg);
    }

}
