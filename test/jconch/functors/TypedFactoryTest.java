package jconch.functors;

import static org.junit.Assert.*;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class TypedFactoryTest {

    @Test(expected = NullArgumentException.class)
    public void constructorExplodesOnFirstArgumentNull() {
        new TypedFactory(null, Integer.class);
    }

    @Test
    public void demonstrateCreate() {
        final Integer in = new Integer(42);
        final TypedFactory<Integer> fixture = new TypedFactory<Integer>(FactoryUtils.constantFactory(in), Integer.class);
        final Integer out = fixture.create();
        assertNotNull("Got null back", out);
        assertSame("Got something back other than what we expect", in, out);
    }

    /**
     * This is just proving behavior used by the code tested by
     * {@link #createExplodesOnBadType()}.
     */
    @Test(expected = ClassCastException.class)
    public void stringCannotBeCastToInteger() {
        final Object o = "42";
        final Integer i = (Integer) o;
        assertNotNull("i is null", i);
    }

    @Test(expected = BadReturnTypeException.class)
    public void createExplodesOnBadType() {
        final TypedFactory<Integer> fixture = new TypedFactory<Integer>(FactoryUtils.constantFactory("42"),
                Integer.class);
        final Object created = fixture.create();
        assertNotNull("Created null", created);
        assertFalse("Created a String -- stupid generics type erasure", String.class.isAssignableFrom(created
                .getClass()));
    }
}
