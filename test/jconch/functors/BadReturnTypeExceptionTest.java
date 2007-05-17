package jconch.functors;

import static org.junit.Assert.*;

import org.junit.Test;

public class BadReturnTypeExceptionTest {

    @Test
    public void classesConstructorPutsSimpleNameIntoMessage() {
        final Class class1 = Integer.class;
        final Class class2 = String.class;
        final String msg = new BadReturnTypeException(class1, class2).getMessage();
        assertNotNull("Message is null", msg);
        assertTrue("Does not contain first argument in message", msg.contains(class1.getSimpleName()));
        assertTrue("Does not contain second argument in message", msg.contains(class2.getSimpleName()));
    }

    @Test
    public void stringConstructorIsPassThrough() {
        final String str = "This is a random string!";
        final String msg = new BadReturnTypeException(str).getMessage();
        assertNotNull("Message is null", msg);
        assertTrue("Message is not what we expect", msg.contains(str));
    }
}
