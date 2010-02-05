package jconch.testing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This ExecutorService never spawns any new threads, it always executes everything it can
 * immedietely. This Executor is not capable of being interrupted.
 *
 * This class is meant to be useful in unit tests.
 *
 * @author Hamlet D'Arcy
 */
public class SerialExecutorService implements ExecutorService {
	private final AtomicBoolean isShutdown = new AtomicBoolean(false);

	public void shutdown() {
		isShutdown.set(true);
	}

	/**
	 *  Marks the executor as shutdown, and now more tasks are accepted for execution.
	 *
	 * @return
	 * 		this ExecutorService is meant to be called from a single thread, so no Runnables
	 * are ever returned. 
	 */
	public List<Runnable> shutdownNow() {
		isShutdown.set(true);
		return new ArrayList<Runnable>(); //is there ever a reason to interrupt running tasks?  
	}

	public boolean isShutdown() {
		return isShutdown.get();  
	}

	public boolean isTerminated() {
		return isShutdown.get();
	}

	/**
	 * Returns immedietly. 
	 * @param timeout
	 * 		ignored
	 * @param unit
	 * 		ignored
	 * @return
	 * 		always true
	 */
	public boolean awaitTermination(long timeout, TimeUnit unit) {
		return true;
	}

	/**
	 * Executes the task immedietly on the current thread.
	 * @param task
	 * 		task to execute, may not be null
	 * @return
	 * 		the result of the task
	 * @throws RuntimeException
	 * 		if task execution failed
	 */
	public <T> Future<T> submit(Callable<T> task) {
		if (isShutdown.get()) throw new RejectedExecutionException(String.format("%s has been shutdown.", getClass().getName()));
		if (task == null) throw new NullPointerException("Null: task");

		try {
			final T result = task.call();	// may throw checked exception
			return new ForcedFuture<T>(result);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Executes the task immedietly on the current thread, returning the result passed in.
	 * @param task
	 * 		task to execute, may not be null
	 * @param result
	 * 		result to return in the future, may be null
	 * @return
	 * 		the result supplied as input
	 */
	public <T> Future<T> submit(Runnable task, T result) {
		if (isShutdown.get()) throw new RejectedExecutionException(String.format("%s has been shutdown.", getClass().getName()));
		if (task == null) throw new NullPointerException("Null: task");
		task.run();
		return new ForcedFuture<T>(result);  
	}

	/**
	 * Executes the task immedietly on the current thread.
	 * @param task
	 * 		task to execute
	 * @return
	 * 		a future that returns null
	 */
	public Future<?> submit(Runnable task) {
		if (isShutdown.get()) throw new RejectedExecutionException(String.format("%s has been shutdown.", getClass().getName()));
		if (task == null) throw new NullPointerException("Null: task");
		task.run();
		return new ForcedFuture<Void>(null);
	}

	/**
	 * Executes all the tasks on the current thread immedietly. Cannot be interrupted.
	 * @param tasks
	 * 		tasks to execute
	 * @return
	 * 		all the futures
	 */
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return doInvokeAll(tasks, new NeverTimeoutStrategy());
	}

	/**
	 * Executes all the tasks on the current thread immedietly. Cannot be interrupted.
	 * @param tasks
	 * 		tasks to execute
	 * @param timeout
	 * 		timeout length
	 * @param unit
	 * 		timeout time unit
	 * @return
	 * 		all the futures, some will be evaluated some will not depending on timeout
	 */
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
		return doInvokeAll(tasks, new TimeoutClockStartingNow(timeout, unit));
	}

	private <T> List<Future<T>> doInvokeAll(Collection<? extends Callable<T>> tasks, TimeoutStrategy timeoutStrategy) {
		if (isShutdown.get()) throw new RejectedExecutionException(String.format("%s has been shutdown.", getClass().getName()));
		if (tasks == null) throw new NullPointerException("Null: tasks");

		final List<Future<T>> completeTasks = new ArrayList<Future<T>>();
		final List<Future<T>> incompleteTasks = new ArrayList<Future<T>>();

		for (Callable<T> task : tasks) {
			if (timeoutStrategy.isTimedOut()) {
				incompleteTasks.add(new FutureTask<T>(task));
			} else {
				try {
					final T result = task.call(); //may throw checked exception
					completeTasks.add(new ForcedFuture<T>(result));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		completeTasks.addAll(incompleteTasks);
		return completeTasks;
	}

	/**
	 * Executes the tasks immedietly on the current thread, returning the first result available that
	 * didn't result in an exception. Cannot be interrupted. Cannot be interrupted.
	 * @param tasks
	 * 		tasks to execute, some of which will not get executed
	 * @return
	 * 		the first result available, other tasks will _not_ be evaluated
	 * @throws ExecutionException
	 * 		if no task ended successfully
	 */
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		try {
			return doInvokeAny(tasks, new NeverTimeoutStrategy());
		} catch (TimeoutException e) {
			// this will never happen given the use of a NeverTimeoutStrategy, 
			// but Java forces the exception to be caught
			throw new ExecutionException(e);
		}
	}

	/**
	 * Executes the tasks immedietly on the current thread, returning the first result available that
	 * didn't result in an exception. Cannot be interrupted.
	 * @param tasks
	 * 		tasks to execute, some of which will not get executed
	 * @param timeout
	 * 		timeout length
	 * @param unit
	 * 		timeout time unit
	 * @return
	 * 		the first result available, other tasks will _not_ be evaluated
	 * @throws ExecutionException
	 * 		if no task ended successfully
	 * @throws TimeoutException
	 * 		if timeout occurs
	 */
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, TimeoutException {
		return doInvokeAny(tasks, new TimeoutClockStartingNow(timeout, unit));
	}

	private <T> T doInvokeAny(Collection<? extends Callable<T>> tasks, TimeoutStrategy timeoutStrategy) throws ExecutionException, TimeoutException {
		if (isShutdown.get()) throw new RejectedExecutionException(String.format("%s has been shutdown.", getClass().getName()));
		if (tasks == null) throw new NullPointerException("Null: tasks");
		if (tasks.size() == 0) throw new IllegalArgumentException();

		Throwable lastError = null;
		for (final Callable<T> task : tasks) {
			if (timeoutStrategy.isTimedOut()) {
				throw new TimeoutException(String.format("Operation timed out on %s", task));
			}
			try {
				return task.call();	//return first value that doesn't throw exception
			} catch (Throwable t) {
				lastError = t;
			}
		}
		throw new ExecutionException("No tasks completed successfully. Posting last exception", lastError);
	}

	/**
	 * Executes the runnable immedietly on the current thread.
	 * @param task
	 * 		task to execute
	 */
	public void execute(Runnable task) {
		if (isShutdown.get()) throw new RejectedExecutionException(String.format("%s has been shutdown.", getClass().getName()));
		if (task == null) throw new NullPointerException("Null: tasks");
		task.run();
	}

	/**
	 * This is a present value that has already been evalutated wrapped in a Future interface.
	 *
	 * @author Hamlet D'Arcy
	 */
	private static final class ForcedFuture<T> implements Future<T> {
		private final T value;

		/**
		 * Creates a "present" which always has a value.
		 * @param value
		 * 		the value the future will return, null allowed
		 */
		private ForcedFuture(T value) {
			this.value = value;	//null values are allowed
		}

		/**
		 * Cancels the future, which is slightly meaningless on an evaluated object.
		 * @param mayInterruptIfRunning
		 * 		ignored
		 * @return
		 * 		always true
		 */
		public boolean cancel(boolean mayInterruptIfRunning) {
			return true;
		}

		/**
		 * An evaluated value is never cancelled before evaluation.
		 * @return
		 * 		always false
		 */
		public boolean isCancelled() {
			return false;
		}

		/**
		 * And evaluated value is always done.
		 * @return
		 * 		always true
		 */
		public boolean isDone() {
			return true;
		}

		/**
		 * Returns the value the Future wraps.
		 * @return
		 * 		the evaluated value
		 */
		public T get() {
			return value;
		}

		/**
		 * Returns the value the Future wraps.
		 * @return
		 * 		the evaluated value
		 */
		public T get(long timeout, TimeUnit unit) {
			return value;  
		}
	}

	/**
	 * Function object for timeouts.
	 */
	private interface TimeoutStrategy {
		
		/**
		 * Tells you if your op is timed out.
		 * @return
		 * 		true if timed out, false otherwise
		 */
		boolean isTimedOut();
	}

	/**
	 *	Function object for an "is timed out" operation.  
	 */
	private static class TimeoutClockStartingNow implements TimeoutStrategy {

		private final long startNanos;
		private final long allowedNanos;

		/**
		 * Creates a timeout clock and starts it ticking right now.
		 * @param timeout
		 * 		timeout length
		 * @param unit
		 * 		time unit
		 */
		private TimeoutClockStartingNow(long timeout, TimeUnit unit) {
			if (unit == null) throw new IllegalArgumentException("Null: unit");
			if (timeout < 0) throw new IllegalArgumentException("timeout must not be negative: " + timeout); 
			startNanos = System.nanoTime();
			allowedNanos = TimeUnit.NANOSECONDS.convert(timeout, unit);
		}

		public boolean isTimedOut() {
			final long currentNanos = System.nanoTime();
			return (currentNanos - startNanos) > allowedNanos;
		}
	}

	/**
	 * This never triggers a timeout. 
	 */
	private static class NeverTimeoutStrategy implements TimeoutStrategy {

		public boolean isTimedOut() {
			return false;
		}
	}
}
