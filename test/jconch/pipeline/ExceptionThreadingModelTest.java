package jconch.pipeline;

import static org.easymock.classextension.EasyMock.createMock;

import org.junit.Test;

public class ExceptionThreadingModelTest {

	@Test(expected = RuntimeException.class)
	public void testExecuteExplodes() {
		new ExceptionThreadingModel().execute(createMock(PipelineStage.class));
	}

}
