package jconch.pipeline.impl;

import java.util.concurrent.ArrayBlockingQueue;

import jconch.pipeline.AbstractPipeLinkTest;
import jconch.pipeline.impl.TypeSafePipeLink;

import org.testng.annotations.Test;

@Test
public class TypeSafePipeLinkBaseTest extends AbstractPipeLinkTest<TypeSafePipeLink<Object>> {

    @Override
    protected TypeSafePipeLink<Object> createFixture() {
        return new TypeSafePipeLink<Object>(new ArrayBlockingQueue<Object>(getMaxCapacity()), Object.class);
    }

    @Override
    protected int getMaxCapacity() {
        return 5;
    }

}
