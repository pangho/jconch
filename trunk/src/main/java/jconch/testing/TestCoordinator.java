
package jconch.testing;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * The TestCoordinator object is meant to provide an easy API to pause and wait for
 * events in unit tests. It abstracts away the details of having to manually manage
 * barriers, latches, or wait/joins.
 *
 * The main API revolves around the delayTestFinish() and finishTest() methods. When you
 * want your test to wait until something happens then call delayTestFinish(). When you want
 * your waiting test to proceed then call delayTestFinish().
 *
 * The examples page is a good place to start exploring how it works, as is the unit test.
 *
 * The TestCoordinator was inspired by GWT's GWTTestCase.
 *
 * @author Hamlet D'Arcy
 * @version $Revision: #1 $ submitted $DateTime: 2008/05/01 13:13:51 $ by $Author: darchb $
 */
class TestCoordinator {

	private final ResettableCountdownLatch latch = new ResettableCountdownLatch(1);


	/**
	 * This method pauses the current thread until finishTest() is called.
	 * @throws RuntimeException
	 * 		if an InterruptedException is thrown then it is wrapped in a RuntimeException
	 */
	public void delayTestFinish() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);	// don't expose InterruptedException to users
		} finally {
			latch.reset();
		}
	}


	/**
	 * This method pauses the current thread until finishTest() is called.
	 *
	 * If finishTest() is not called within the specified time frame then the thread is allowed to proceed.
	 *
	 * @param timeout
	 * 		the number of milliseconds to wait until giving up and proceeding
	 * @return
	 * 		true if the timeout value was exceeded
	 * @throws RuntimeException
	 * 		if an InterruptedException is thrown then it is wrapped in a RuntimeException
	 */
	public boolean delayTestFinish(int timeout) {
		try {
			return latch.await(timeout);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);	// don't expose InterruptedException to users
		} finally {
			latch.reset();
		}
	}


	/**
	 * This method pauses the current thread until finishTest() is called.
	 *
	 * If finishTest() is not called within the specified time frame then the thread is allowed to proceed.
	 *
	 * @param timeout
	 * 		the number of units to wait until giving up and proceeding
	 * @param timeUnit
	 * 		the unit of measure that defines timeout
	 * @return
	 * 		true if the timeout value was exceeded
	 * @throws RuntimeException
	 * 		if an InterruptedException is thrown then it is wrapped in a RuntimeException
	 */
	public boolean delayTestFinish(int timeout, TimeUnit timeUnit) {
		try {
			return latch.await(timeout, timeUnit);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);	// don't expose InterruptedException to users
		} finally {
			latch.reset();
		}
	}


	/**
	 * This method tells the coordinator that the test is finished and any waiting party can proceed.
	 */
	public void finishTest() {
		latch.countDown();
	}


	/**
	 * A {@linkplain CountDownLatch CountDownLatch} that supports resets.
	 * JDK 7 is slated to have something similar in its "Phase" objects.
	 * @author Hamlet D'Arcy
	 */
	private static class ResettableCountdownLatch {
		private final int parties;
		private final AtomicReference<CountDownLatch> ref = new AtomicReference<CountDownLatch>();


		/**
		 * Creates the countdown latch.
		 * @param parties
		 * 		number of parties that will participate in the latch
		 */
		private ResettableCountdownLatch(int parties) {
			this.parties = parties;
			reset();
		}


		/**
		 * Resets the latch back to the original number of parties.
		 */
		private void reset() {
			ref.set(new CountDownLatch(parties));
		}


		/**
		 * Causes the current thread to wait until the latch has counted down to
		 * zero, unless the thread is {@linkplain Thread#interrupt interrupted}.
		 *
		 * <p>If the current count is zero then this method returns immediately.
		 *
		 * <p>If the current count is greater than zero then the current
		 * thread becomes disabled for thread scheduling purposes and lies
		 * dormant until one of two things happen:
		 * <ul>
		 * <li>The count reaches zero due to invocations of the
		 * {@link #countDown} method; or
		 * <li>Some other thread {@linkplain Thread#interrupt interrupts}
		 * the current thread.
		 * </ul>
		 *
		 * <p>If the current thread:
		 * <ul>
		 * <li>has its interrupted status set on entry to this method; or
		 * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
		 * </ul>
		 * then {@link InterruptedException} is thrown and the current thread's
		 * interrupted status is cleared.
		 *
		 * @throws InterruptedException if the current thread is interrupted
		 *         while waiting
		 */
		private void await() throws InterruptedException {
			ref.get().await();
		}


		/**
		 * Causes the current thread to wait until the latch has counted down to
		 * zero, unless the thread is {@linkplain Thread#interrupt interrupted},
		 * or the specified waiting time elapses.
		 *
		 * <p>If the current count is zero then this method returns immediately
		 * with the value {@code true}.
		 *
		 * <p>If the current count is greater than zero then the current
		 * thread becomes disabled for thread scheduling purposes and lies
		 * dormant until one of three things happen:
		 * <ul>
		 * <li>The count reaches zero due to invocations of the
		 * {@link #countDown} method; or
		 * <li>Some other thread {@linkplain Thread#interrupt interrupts}
		 * the current thread; or
		 * <li>The specified waiting time elapses.
		 * </ul>
		 *
		 * <p>If the count reaches zero then the method returns with the
		 * value {@code true}.
		 *
		 * <p>If the current thread:
		 * <ul>
		 * <li>has its interrupted status set on entry to this method; or
		 * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
		 * </ul>
		 * then {@link InterruptedException} is thrown and the current thread's
		 * interrupted status is cleared.
		 *
		 * <p>If the specified waiting time elapses then the value {@code false}
		 * is returned.  If the time is less than or equal to zero, the method
		 * will not wait at all.
		 *
		 * @param timeout the maximum time to wait in milliseconds
		 * @return {@code true} if the count reached zero and {@code false}
		 *         if the waiting time elapsed before the count reached zero
		 * @throws InterruptedException if the current thread is interrupted
		 *         while waiting
		 */
		private boolean await(int timeout) throws InterruptedException {
			return ref.get().await(timeout, TimeUnit.MILLISECONDS);
		}


		/**
		 * Causes the current thread to wait until the latch has counted down to
		 * zero, unless the thread is {@linkplain Thread#interrupt interrupted},
		 * or the specified waiting time elapses.
		 *
		 * <p>If the current count is zero then this method returns immediately
		 * with the value {@code true}.
		 *
		 * <p>If the current count is greater than zero then the current
		 * thread becomes disabled for thread scheduling purposes and lies
		 * dormant until one of three things happen:
		 * <ul>
		 * <li>The count reaches zero due to invocations of the
		 * {@link #countDown} method; or
		 * <li>Some other thread {@linkplain Thread#interrupt interrupts}
		 * the current thread; or
		 * <li>The specified waiting time elapses.
		 * </ul>
		 *
		 * <p>If the count reaches zero then the method returns with the
		 * value {@code true}.
		 *
		 * <p>If the current thread:
		 * <ul>
		 * <li>has its interrupted status set on entry to this method; or
		 * <li>is {@linkplain Thread#interrupt interrupted} while waiting,
		 * </ul>
		 * then {@link InterruptedException} is thrown and the current thread's
		 * interrupted status is cleared.
		 *
		 * <p>If the specified waiting time elapses then the value {@code false}
		 * is returned.  If the time is less than or equal to zero, the method
		 * will not wait at all.
		 *
		 * @param timeout the maximum time to wait
		 * @param unit the time unit of the {@code timeout} argument
		 * @return {@code true} if the count reached zero and {@code false}
		 *         if the waiting time elapsed before the count reached zero
		 * @throws InterruptedException if the current thread is interrupted
		 *         while waiting
		 */
		private boolean await(int timeout, TimeUnit unit) throws InterruptedException {
			return ref.get().await(timeout, unit);
		}


		/**
		 * Decrements the count of the latch, releasing all waiting threads if
		 * the count reaches zero.
		 *
		 * <p>If the current count is greater than zero then it is decremented.
		 * If the new count is zero then all waiting threads are re-enabled for
		 * thread scheduling purposes.
		 *
		 * <p>If the current count equals zero then nothing happens.
		 */
		private void countDown() {
			ref.get().countDown();
		}
	}
}
