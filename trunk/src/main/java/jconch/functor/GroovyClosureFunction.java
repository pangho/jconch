package jconch.functor;

import groovy.lang.Closure;

import org.apache.commons.lang.NullArgumentException;
import com.google.common.base.Function;

public class GroovyClosureFunction implements Function<Object, Object> {

    private final Closure impl;

    public GroovyClosureFunction(final Closure closure) {
        if (closure == null) {
            throw new NullArgumentException("closure");
        }
        impl = closure;
    }

    public Object apply(final Object arg) {
        return impl.call(arg);
    }

}
