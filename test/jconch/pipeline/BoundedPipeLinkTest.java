package jconch.pipeline;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BoundedPipeLinkTest extends AbstractPipeLinkTest<BoundedPipeLink<Object>> {

    @Override
    protected BoundedPipeLink<Object> createFixture() {
        return new BoundedPipeLink<Object>(getMaxCapacity());
    }

    @Override
    protected int getMaxCapacity() {
        return 10000;
    }

    @Test
    public void getJUnit4ToSeeThisTest() {
        assertTrue(true);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorExplodesOnZeroCapacity() {
        new BoundedPipeLink<Object>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorExplodesOnNegativeCapacity() {
        new BoundedPipeLink<Object>(-1);
    }

}
