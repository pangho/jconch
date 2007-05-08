package jconch.functors;

import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.NullArgumentException;

/**
 * A class that wraps {@link Transformer} and provides typing.
 * 
 * @author Robert Fischer
 * 
 * @param <IN_T>
 *            The type of the argument being passed in.
 * @param <OUT_T>
 *            The type of the return value
 */
public class TypedTransformer<IN_T, OUT_T> implements Transformer {

	/**
	 * The implementation of the type
	 */
	private final Transformer transformer;

	/**
	 * Constructor.
	 * 
	 * @param in
	 *            The class of the parameter.
	 * @param impl
	 *            The implementation of the transformer.
	 * @param out
	 *            The return value of the transformer.
	 * @throws NullArgumentException
	 *             If any argument is <code>null</code>.
	 */
	public TypedTransformer(final Transformer impl) {
		if (impl == null) {
			throw new NullArgumentException("impl");
		}
		transformer = impl;
	}

	/**
	 * Executes the transform.
	 * 
	 * @param param
	 *            The argument for the implementation.
	 * @throws BadReturnTypeException
	 *             If the implementation does not return an object of type
	 *             <code>OUT_T</code>
	 */
	public OUT_T typedTransform(IN_T param) {
		// Fetch the object first, because we want to allow exceptions to
		// propogate out. We don't want to accidentally mis-catch a CCE.
		final Object out = transformer.transform(param);
		try {
			return (OUT_T) out;
		} catch (ClassCastException cce) {
			throw new BadReturnTypeException(
					"Typed transformer does not accept return type "
							+ out.getClass().getSimpleName());
		}
	}

	/**
	 * Executes the transform. This is the required signature for the
	 * {@link Transformer}.
	 * 
	 * @param param
	 *            The argument for the implementation.
	 * @throws IllegalClassException
	 *             If <code>param</code> is not of type <code>IN_T</code>
	 * @throws BadReturnTypeException
	 *             If the implementation does not return an object of type
	 *             <code>OUT_T</code>
	 */
	public Object transform(Object param) {
		final IN_T in;
		try {
			in = (IN_T) param;
		} catch (ClassCastException cce) {
			throw new IllegalClassException(
					"Typed transformer does not accept input type "
							+ param.getClass().getSimpleName());
		}
		return typedTransform(in);
	}
}
