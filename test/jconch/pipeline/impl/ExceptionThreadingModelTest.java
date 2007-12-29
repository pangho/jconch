package jconch.pipeline.impl;

import static org.easymock.classextension.EasyMock.*;
import jconch.pipeline.PipelineStage;

import org.testng.annotations.Test;

public class ExceptionThreadingModelTest {

	@Test(expectedExceptions = RuntimeException.class)
	public void testExecuteExplodes() {
		new ExceptionThreadingModel().execute(createMock(PipelineStage.class));
	}

}
