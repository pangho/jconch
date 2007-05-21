package jconch.pipeline.impl;

import static org.easymock.classextension.EasyMock.createMock;
import jconch.pipeline.PipelineStage;

import org.junit.Test;

public class ExceptionThreadingModelTest {

    @Test(expected = RuntimeException.class)
    public void testExecuteExplodes() {
        new ExceptionThreadingModel().execute(createMock(PipelineStage.class));
    }

}
