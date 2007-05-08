package jconch.functors;

/**
 * Thrown when a functor returns a type that was not expected.
 * 
 * @author Robert Fischer
 */
public class BadReturnTypeException extends ClassCastException {

	/**
	 * {@link Class}-based constructor.
	 * 
	 * @param expected
	 *            The class expected to be returned.
	 * @param actual
	 *            The type that was returned.
	 */
	public BadReturnTypeException(final Class expected, final Class actual) {
		super("Return type was inappropriate: expected "
				+ expected.getSimpleName() + ", received "
				+ actual.getSimpleName());
	}

	/**
	 * Message-based constructor.
	 * 
	 * @param message
	 *            The message to include on the String.
	 */
	public BadReturnTypeException(final String message) {
		super(message);
	}
}
