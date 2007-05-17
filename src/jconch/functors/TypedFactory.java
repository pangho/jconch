package jconch.functors;

import org.apache.commons.collections.Factory;
import org.apache.commons.lang.NullArgumentException;

/**
 * Wraps {@link Factory} to provide type safety.
 * 
 * <p>
 * <a href="TypedFactory.java.html">View Source</a>
 * </p>
 * 
 * @author rfischer
 * @version $Date: May 16, 2007 8:02:10 AM $
 */
public class TypedFactory<T> implements Factory {

    private final Factory impl;

    private final Class clz;

    /**
     * Constructor.
     * 
     * @param impl
     *            The underlying implementation to use.
     * @param outClass
     *            The class this factory should generate.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>.
     */
    public TypedFactory(final Factory impl, final Class<T> outClass) {
        if (impl == null) {
            throw new NullArgumentException("impl");
        }
        if (outClass == null) {
            throw new NullArgumentException("outClass");
        }
        this.impl = impl;
        this.clz = outClass;
    }

    public T create() {
        final Object created = impl.create();
        if (created == null || clz.isAssignableFrom(created.getClass())) {
            return (T) created;
        }
        throw new BadReturnTypeException("Typed factory does not accept type" + created.getClass().getSimpleName());
    }
}
