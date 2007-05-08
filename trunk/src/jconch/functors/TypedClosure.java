package jconch.functors;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.NullArgumentException;

/**
 * A class that wraps {@link Closure} and provides typing.
 * 
 * @author Robert Fischer
 * 
 * @param <IN_T>
 *            The type of the argument being passed in.
 * 
 */
public class TypedClosure<IN_T> implements Closure {

	/**
	 * The wrapped implementation
	 */
	private final Closure closure;

	/**
	 * Constructor.
	 * 
	 * @param impl
	 *            The implementation of the closure.
	 * @throws NullArgumentException
	 *             If either argument is <code>null</code>
	 */
	public TypedClosure(final Closure impl) {
		if (impl == null) {
			throw new NullArgumentException("impl");
		}
		this.closure = impl;
	}

	/**
	 * Executes the closure.
	 * 
	 * @param param
	 *            The argument for the implementation.
	 */
	public void typedExecute(final IN_T param) {
		this.closure.execute(param);
	}

	/**
	 * Executes the closure. This is the required signature for {@link Closure}.
	 * 
	 * @param param
	 *            The argument for the implementation.
	 * @throws IllegalClassException
	 *             If <code>param</code> is not of type <code>IN_T</code>
	 */
	public void execute(final Object param) {
		final IN_T in;
		try {
			in = (IN_T) param;
		} catch (ClassCastException cce) {
			throw new IllegalClassException(
					"Typed closure does not accept input type "
							+ param.getClass().getSimpleName());
		}
		typedExecute(in);
	}
}
