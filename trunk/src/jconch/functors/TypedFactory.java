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

    /**
     * Constructor.
     * 
     * @param impl
     *            The underlying implementation to use.
     * @throws NullArgumentException
     *             If the argument is <code>null</code>.
     */
    public TypedFactory(final Factory impl) {
        if (impl == null) {
            throw new NullArgumentException("impl");
        }
        this.impl = impl;
    }

    public T create() {
        final Object created = impl.create();
        try {
            return (T) created;
        } catch (ClassCastException cce) {
            throw new BadReturnTypeException("Typed factory does not accept type" + created.getClass().getSimpleName());
        }
    }
}
