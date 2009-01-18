package jconch.testing;


import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A collection of helpful concurrency based assert methods.
 *
 * @author Hamlet D'Arcy
 */
public class Assert {
	private static final int MAX_THREADS = 10;
	private static final int NUM_ITERATIONS = 1000;

	private Assert() {
		throw new AssertionError(); 
	}

	/**
	 * <p>The assertSynchronized method attempts to cause concurrency errors by repeatedly invoking code on seperate threads. </p>
	 *
	 * <p>To use this assertion, give the method a {@link java.util.concurrent.Callable} object that produces a Collection
	 * of code fragments which must be synchronized between themselves. This master callable that produces the code
	 * fragments is called the "taskFactory", and it will be invoked repeatedly, running all the resulting Callable
	 * objects on seperate threads. </p>
	 *
	 * <p>As an example, the {@link java.util.ArrayList#add(Object)}  method is not synchronized. So add invocations on
	 * multiple threads will eventually result in an ArrayIndexOutOfBoundsException. To test this, pass
	 * assertSynchronized a factory for Callables that call add():</p>
	 *
	 * <pre>
	 * 
     * Assert.assertSynchronized(
     *   new Callable&lt;List&lt;Callable&lt;Void&gt;&gt;&gt;() {
     *     public List&lt;Callable&lt;Void&gt;&gt; call() throws Exception {
     *
     *       // ArrayList.add(T) is not synchronized
     *       final ArrayList&lt;Object&gt; unsafeObject = new ArrayList&lt;Object&gt;();
     *       final Callable&lt;Void&gt; unsafeInvocation = new Callable&lt;Void&gt;() {
     *         public Void call() throws Exception {
     *           for (int x = 0; x &lt; 1000; x++) {
     *             unsafeObject.add(new Object());
     *           }
     *           return null;
     *         }
     *       };
     *       return Arrays.asList(
     *         unsafeInvocation,
     *         unsafeInvocation
     *       );
     *     }
     *   }
     * );</pre>
	 * <p>This will fail with an AssertionError as expected. The test will pass if you switch the implementation
	 * to a {@link java.util.Vector} instead of an ArrayList</p>
	 *  
	 * <p>By default, it creates a thread pool of 10 threads, and attempts to run the code passed in 1000 times. Any errors
	 * that occur are collected and reported in an {@link java.lang.AssertionError}.</p>
	 *  
	 * @param taskFactory
	 * 		a Callable that produces a new Collection of Callable objects on each iteration. Expect this Callable to
	 * 		be invoked x number of times, where x is the numIterations value. May not be null.
	 * @throws Exception
	 * 		Any exception raised by the taskFactory will be passed up to the caller
	 * @throws AssertionError
	 * 		If any of the callables raises an exception, then as AssertionError is thrown. The error will report how
	 * 		many failures occurred and the message of one of the thrown exceptions.  The message will usually (but not always)
	 * 		be that of the most recently thrown exception.
	 */
	public static void assertSynchronized(Callable<? extends Collection<Callable<Void>>> taskFactory) throws Exception {
		doAssertSynchronized(taskFactory, MAX_THREADS, NUM_ITERATIONS);
	}

	/**
	 * <p>This is the same as {@link Assert#assertSynchronized(Callable)} expect that the thread pool size and the number
	 * or iterations can be specified.</p>  
	 * @param taskFactory
	 * 		a Callable that produces a new Collection of Callable objects on each iteration. Expect this Callable to
	 * 		be invoked x number of times, where x is the numIterations value. May not be null. 
	 * @param threadPoolSize
	 * 		size of the threadpool used during testing, must be positive
	 * @param numIterations
	 * 		the number of times to run the test. This number needs to be sufficiently large emough to cause the error
	 * 		condition, whether it be a race condition, phantom data, or anything else. Must be positive. 
	 * @throws Exception
	 * 		Any exception raised by the taskFactory will be passed up to the caller
	 * @throws AssertionError
	 * 		If any of the callables raises an exception, then as AssertionError is thrown. The error will report how
	 * 		many failures occurred and the message of one of the thrown exceptions.  The message will usually (but not always)
	 * 		be that of the most recently thrown exception. 
	 */
	public static void assertSynchronized(Callable<? extends Collection<Callable<Void>>> taskFactory, int threadPoolSize, int numIterations) throws Exception {
		doAssertSynchronized(taskFactory, threadPoolSize, numIterations);
	}

	private static void doAssertSynchronized(Callable<? extends Collection<Callable<Void>>> taskFactory, int maxThreads, int numIterations) throws Exception {
		if (taskFactory == null) throw new NullPointerException("Null: taskFactory");
		if (maxThreads <= 0) throw new IllegalArgumentException("maxThreads must be positive. Received: " + maxThreads);
		if (numIterations <= 0) throw new IllegalArgumentException("numIterations must be positive. Received: " + numIterations); 

		final ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
		final AtomicInteger failureCount = new AtomicInteger(0);
		final AtomicReference<Throwable> lastFailure = new AtomicReference<Throwable>();

		for (int x = 0; x < numIterations; x++) {
			final Collection<Callable<Void>> tasks = taskFactory.call();
			final int numTasks = tasks.size();
			final CyclicBarrier startGate = new CyclicBarrier(Math.min(numTasks, maxThreads));
			final CountDownLatch endGate = new CountDownLatch(numTasks);

			for (final Callable task : tasks) {

				executor.submit(new Callable<Void>(){
					public Void call() throws Exception {
						try {
							startGate.await();
							task.call();
						} catch (Throwable t) {
							failureCount.incrementAndGet();
							lastFailure.set(t);	// race conditions here are no big deal
						} finally {
							endGate.countDown();
						}
						return null;
					}
				});
			}

			endGate.await();
		}
		executor.shutdown();
		if (failureCount.get() > 0) {
			final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			lastFailure.get().printStackTrace(new PrintStream(bytes));

			throw new AssertionError(
					String.format(
						"An exception was raised running the synchronization test. " +
						"The test failed %d out of %d times. Last known error:\n%s",
						failureCount.get(),
						numIterations,
						bytes.toString()));
		}
	}
}
