package eg.jconch.testing;

import jconch.testing.SerialExecutorService;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Example usage of the {@link jconch.testing.SerialExecutorService}.
 */
public class SerialExecutorServiceExample {

	@Test
	public void testSerialExecutionWorks() {

		final ExecutorService executor = new SerialExecutorService();
		final ClassThatSpawnsWork worker = new ClassThatSpawnsWork(executor);
		worker.multiplyService(25, 4);
		assertEquals(100, worker.lastResult[0].intValue());
	}

	@Test(expectedExceptions = NullPointerException.class)
	public void testThreadedExecutionBroken() {

		final ExecutorService executor = Executors.newSingleThreadExecutor();
		final ClassThatSpawnsWork worker = new ClassThatSpawnsWork(executor);
		worker.multiplyService(25, 4);
		assertEquals(100, worker.lastResult[0].intValue());
	}

	/**
	 * A simple class that spawns work asynchronously. Not production code by any means.
	 */
	private static class ClassThatSpawnsWork {

		private final ExecutorService executor;
		private Integer[] lastResult = new Integer[1];

		public ClassThatSpawnsWork(ExecutorService executor) {
			this.executor = executor;
		}

		void multiplyService(final int factor1, final int factor2) {
			executor.submit(new Callable<Void>(){
				public Void call() throws Exception {
					Thread.sleep(1000);		//lengthy computation
					lastResult[0] = factor1 * factor2;
					return null;
				}
			});
		}
	}
}
