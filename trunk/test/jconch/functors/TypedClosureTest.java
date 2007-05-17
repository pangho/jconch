package jconch.functors;

import static org.junit.Assert.*;

import org.apache.commons.collections.Closure;
import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class TypedClosureTest {

    @Test(expected = NullArgumentException.class)
    public void constructorExplodesOnNull() {
        new TypedClosure(null);
    }

    @Test
    public void demonstrateTypedExecute() {
        final CheckMeClosure ctrl = new CheckMeClosure();
        final TypedClosure<Object> fixture = new TypedClosure<Object>(ctrl);
        final Object testMonkey = new Object();
        fixture.typedExecute(testMonkey);
        assertTrue("Was not called", ctrl.called);
        assertSame("Arg was wrong", testMonkey, ctrl.arg);
    }

    @Test
    public void demonstrateExecute() {
        final CheckMeClosure ctrl = new CheckMeClosure();
        final TypedClosure<Object> fixture = new TypedClosure<Object>(ctrl);
        final Object testMonkey = new Object();
        fixture.execute(testMonkey);
        assertTrue("Was not called", ctrl.called);
        assertSame("Arg was wrong", testMonkey, ctrl.arg);
    }

    @Test(expected = IllegalClassException.class)
    public void executeExplodesOnBadInputClass() {
        final CheckMeClosure ctrl = new CheckMeClosure();
        final TypedClosure<Integer> fixture = new TypedClosure<Integer>(ctrl);
        final Object testMonkey = new Object();
        fixture.execute(testMonkey);
    }

    private static final class CheckMeClosure implements Closure {

        public Object arg = null;

        public boolean called = false;

        public void execute(Object arg0) {
            arg = arg0;
            called = true;
        }

    }
}
