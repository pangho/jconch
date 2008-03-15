package jconch.pipeline.impl;

import static org.easymock.EasyMock.*;

import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang.IllegalClassException;
import org.apache.commons.lang.NotImplementedException;
import org.testng.annotations.Test;

public class TypeSafePipeLinkExtensionTest {

    @SuppressWarnings("unchecked")
    @Test(expectedExceptions = IllegalClassException.class)
    public void testRuntimeExplosionIfBadClass() {
        final BlockingQueue mock = createNiceMock(BlockingQueue.class);
        replay(mock);
        new TypeSafePipeLink(mock, BToy.class).add(1);
    }

    @Test(expectedExceptions = IllegalClassException.class)
    public void testRuntimeExplosionIfSuperclassPassedIn() {
        throw new NotImplementedException();
    }

    @Test
    public void testNoRuntimeExplosionOnNormalClass() {
        throw new NotImplementedException();
    }

    @Test
    public void testNoRuntimeExposionOnSubclass() {
        throw new NotImplementedException();
    }

}

class AToy {
    // EMPTY CLASS
}

class BToy extends AToy {
    // EMPTY CLASS
}

class CToy extends BToy {
    // EMPTY CLASS
}