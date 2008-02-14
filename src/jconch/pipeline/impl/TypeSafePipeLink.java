package jconch.pipeline.impl;

import java.util.concurrent.BlockingQueue;

import jconch.pipeline.PipeLink;

import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.NullArgumentException;

/**
 * Due to a limitation in Java's generic implementation, there is no way to go
 * from a parameterized type to a {@link Class} instance representing that type.
 * And since type erasure undermines casts to parameterized types, there is no
 * run-time type safety guarantied through parameterized types alone.
 * <p />
 * This class works around that by taking in a {@link Class} instance which can
 * then be used to perform run-time type checks.
 */
public class TypeSafePipeLink<T> extends PipeLink<T> {

    private final Class<T> eltClass;

    /**
     * {@inheritDoc}
     * 
     * @param elementClass
     *            The class of elements to be added.
     * @throws NullArgumentException
     *             If <code>elementClass</code> is <code>null</code>.
     */
    protected TypeSafePipeLink(final BlockingQueue<T> queue, final Class<T> elementClass) {
        super(queue);
        this.eltClass = elementClass;
    }

    /**
     * {@inheritDoc}
     * 
     * @throws IllegalClassException
     *             If in is not an element of the type represented by
     *             {@link #getElementClass()}.
     */
    @Override
    public boolean add(final T in) {
        if (in != null) {
            final Class<?> inClass = in.getClass();
            if (!eltClass.isAssignableFrom(inClass)) {
                throw new IllegalClassException(eltClass, inClass);
            }
        }
        return super.add(in);
    }

    /**
     * Provides the element class.
     * 
     * @return The {@link Class} instance representing the contract that classes
     *         must adhere to. Never <code>null</code>.
     */
    public Class<T> getElementClass() {
        return this.eltClass;
    }

}
