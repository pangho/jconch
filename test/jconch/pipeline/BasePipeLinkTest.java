package jconch.pipeline;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.lang.NullArgumentException;
import org.junit.Test;

public class BasePipeLinkTest extends AbstractPipeLinkTest {

    @Override
    protected PipeLink createFixture() {
        return new PipeLink<Object>(new ArrayBlockingQueue<Object>(getMaxCapacity()));
    }

    @Override
    protected int getMaxCapacity() {
        return 1000;
    }

    @Test
    public void junit4NeedsToNoticeThisIsATest() {
        assertTrue(true);
    }

    @Test(expected = NullArgumentException.class)
    public void pipeLinkConstructorExplodesOnNullArg() {
        new PipeLink<Object>(null);
    }

}
