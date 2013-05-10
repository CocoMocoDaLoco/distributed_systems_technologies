package dst.ass3.util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

/**
 * Contains some convenience methods for messages handling and event processing.
 */
public final class Utils {
	public static final int SHORT_WAIT = 200;

	private Utils() {
	}

	/**
	 * Logs the given string to the console.
	 *
	 * @param msg the message to log
	 */
	public static void log(String msg) {
		System.out.println(msg);
	}

	/**
	 * Attempts to cause the currently executing thread to sleep (temporarily cease execution) for the specified number
	 * of milliseconds, subject to the precision and accuracy of system timers and schedulers.
	 * <p/>
	 * If any thread has interrupted the current thread, the {@link InterruptedException} is caught silently and this
	 * method returns immediately.
	 *
	 * @param millis the length of time to sleep in milliseconds
	 * @throws IllegalArgumentException if the value of millis is negative
	 */
	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			// may happen
		}
	}

	/**
	 * Logs the checkpoint + relative time since start.
	 *
	 * @param checkpoint the number of the checkpoint to log
	 * @param startTime  the time reference
	 */
	public static void logCheckpoint(int checkpoint, long startTime) {
		logTimed("**** CHECKPOINT " + checkpoint + " ****", startTime);
	}

	/**
	 * Logs the msg [+<time since startTime> sec].
	 *
	 * @param msg       the message to print
	 * @param startTime the time reference
	 */
	public static void logTimed(String msg, long startTime) {
		long diff = new Date().getTime() - startTime;
		DecimalFormat df = new DecimalFormat("#.#");
		log(msg + " [+" + df.format(diff / 1000.0) + " sec]");
	}

	/**
	 * Makes the semaphore access more easy to read in the tests.
	 *
	 * @param sem            the semaphore used to acquire
	 * @param count          the number of permits to try to acquire
	 * @param failMessage    fail(failMessage) if timeouts not met
	 * @param timeoutSeconds the timeout for the acquire operation in seconds
	 * @see #assureMin(java.util.concurrent.Semaphore, int, String, int, int, boolean)
	 */
	public static void assure(Semaphore sem, int count, String failMessage, int timeoutSeconds) {
		assureMin(sem, count, failMessage, 0, timeoutSeconds, true);
	}

	/**
	 * Like assure but also assures that it takes an minimum given in seconds
	 * to acquire the locks. the timeout should be > minimum time
	 *
	 * @param sem            the semaphore used to acquire
	 * @param count          the number of permits to try to acquire
	 * @param failMessage    fail(failMessage) if timeouts not met
	 * @param minimumTime    the minimum time the acquire operation has to take
	 * @param timeoutSeconds the timeout for the acquire operation in seconds
	 * @param exact          count should be exactly the available count
	 */
	public static void assureMin(Semaphore sem, int count, String failMessage, int minimumTime,
								 int timeoutSeconds, boolean exact) {
		try {
			long start = new Date().getTime();
			if (sem.tryAcquire(count, timeoutSeconds, TimeUnit.SECONDS) == false) {
				fail(failMessage + " available:" + sem.availablePermits());
			} else {
				if (sem.availablePermits() > 0 && exact) {
					fail("exact (avail:" + sem.availablePermits() + "):" + failMessage);
				}
				// success - check if minimum timespan is violated
				long now = new Date().getTime();
				double timeDiff = (int) ((now - start) / 1000.0);
				DecimalFormat df = new DecimalFormat("#.#");
				if (timeDiff < minimumTime) {
					fail(failMessage + " MIN-TIMEOUT-VIOLATION(exp:" + minimumTime + " / act:"
							+ df.format(timeDiff) + ")");
				}
			}
		} catch (InterruptedException e) {
			fail(failMessage);
		}
	}
}
