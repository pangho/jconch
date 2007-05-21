package jconch.pipeline.impl;

import static org.junit.Assert.assertTrue;
import jconch.pipeline.AbstractPipeLinkTest;
import jconch.pipeline.impl.UnboundedPipeLink;

import org.junit.Test;

public class UnboundedPipeLinkTest extends AbstractPipeLinkTest<UnboundedPipeLink<Object>> {

    @Override
    protected UnboundedPipeLink<Object> createFixture() {
        return new UnboundedPipeLink<Object>();
    }

    @Override
    protected int getMaxCapacity() {
        return Integer.MAX_VALUE;
    }

    @Test
    public void thisIsAJUnitTestReally() {
        assertTrue(true);
    }
}
