package jconch.testing;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Test for {@link jconch.testing.SerialExecutorService}.
 *
 * @author Hamlet D'Arcy
 */
public class SerialExecutorServiceTest {
	private SerialExecutorService service;

	@BeforeMethod
	protected void setUp() throws Exception {
		service = new SerialExecutorService();
	}

	@Test
	public void shutdown() throws Exception {
		assertFalse(service.isShutdown());
		assertFalse(service.isTerminated());

		service.shutdown();

		assertTrue(service.isShutdown());
		assertTrue(service.isTerminated());

		try {
			service.submit(new ErrorCallable());
		} catch (RejectedExecutionException ignored) {
			//expected
		}
		try {
			service.submit(new ErrorRunnable(), null);
		} catch (RejectedExecutionException ignored) {
			//expected
		}
		try {
			service.submit(new ErrorRunnable());
		} catch (RejectedExecutionException ignored) {
			//expected
		}
		try {
			service.invokeAll(null);
		} catch (RejectedExecutionException ignored) {
			//expected
		}
		try {
			service.invokeAll(null, 0, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ignored) {
			//expected
		}
		try {
			service.invokeAny(null);
		} catch (RejectedExecutionException ignored) {
			//expected
		}
		try {
			service.invokeAny(null, 0, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException ignored) {
			//expected
		}

	}

	@Test
	public void testSubmit() throws Exception {
		final Object expectedResult = new Object();
		assertSame(expectedResult, service.submit(new ValueCallable(expectedResult)).get());
		final DummyRunnable dummyRunnable = new DummyRunnable();
		assertNull(((Future<?>) service.submit(dummyRunnable)).get());
		assertTrue(dummyRunnable.wasCalled());
		final DummyRunnable dummyRunnable2 = new DummyRunnable();
		assertSame(expectedResult, service.submit(dummyRunnable2, expectedResult).get());
		assertTrue(dummyRunnable2.wasCalled());
	}

	@Test
	public void testExecute() throws Exception {
		final DummyRunnable runnable = new DummyRunnable();
		service.execute(runnable);
		assertTrue(runnable.wasCalled());
	}

	@Test
	public void testInvokeAll() throws Exception {
		final Object value1 = new Object();
		final Object value2 = new Object();
		final Callable<Object> task1 = new ValueCallable(value1);
		final Callable<Object> task2 = new ValueCallable(value2);
		final List<Future<Object>> futures = service.invokeAll(Arrays.asList(task1, task2));

		assertEquals(2, futures.size());
		assertSame(value1, futures.get(0).get());
		assertSame(value2, futures.get(1).get()); 
	}

	@Test
	public void testInvokeAll_Timeout() throws Exception {
		final Object value1 = new Object();
		final Object value2 = new Object();
		final Callable<Object> task1 = new ValueCallable(value1);
		final Callable<Object> task2 = new ValueCallable(value2);
		final List<Future<Object>> futures = service.invokeAll(Arrays.asList(task1, task2), 5000, TimeUnit.MILLISECONDS);

		assertEquals(2, futures.size());
		assertSame(value1, futures.get(0).get());
		assertSame(value2, futures.get(1).get());
	}

	@Test public void testInvokeAny() throws Exception {
		final Object expectedResult = new Object();
		final Callable<Object> task1 = new ValueCallable(expectedResult);
		final Callable<Object> task2 = new ValueCallable(new Object());
		final Object result = service.invokeAny(Arrays.asList(task1, task2));

		assertSame(expectedResult, result);
	}

	@Test public void testInvokeAny_Timeout() throws Exception {
		final Object expectedResult = new Object();
		final Callable<Object> task1 = new ValueCallable(expectedResult);
		final Callable<Object> task2 = new ValueCallable(new Object());
		final Object result = service.invokeAny(Arrays.asList(task1, task2), 5000, TimeUnit.MILLISECONDS);

		assertSame(expectedResult, result);
	}
	
	private static class ErrorCallable implements Callable<Object> {

		public Object call() throws Exception {
			throw new IllegalStateException(String.format("%s may not be invoked.", getClass().getName()));
		}
	}

	private static class ValueCallable implements Callable<Object> {
		private final Object value;

		private ValueCallable(Object value) {
			this.value = value;
		}

		public Object call() throws Exception {
			return value;
		}
	}
	private static class ErrorRunnable implements Runnable {

		public void run() {
			throw new IllegalStateException(String.format("%s may not be invoked.", getClass().getName()));
		}
	}

}
