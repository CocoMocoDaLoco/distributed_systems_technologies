package dst.ass3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.event.MappedEventBean;

import static org.junit.Assert.fail;

/**
 * Contains some utility methods for event processing.
 */
public final class EventingUtils {
	/* settings */

	/**
	 * Time to wait for semaphores to reach required value
	 */
	public static final int ESPER_CHECK_TIMEOUT = 3;

	private EventingUtils() {
	}

	/**
	 * Checks if {@code jobId} from bean matches ANY in {@code compareWith}.
	 * </p> If it does not match, an {@link AssertionError} is thrown.
	 * 
	 * @param failMsg
	 *            the error message
	 * @param e
	 *            the event bean
	 * @param compareWith
	 *            the list of possible jobIds
	 */
	public static void ensureJobId(String failMsg, EventBean e,
			long... compareWith) {
		StringBuffer couldBe = new StringBuffer();
		try {

			Long jobId = getLong(e, "jobId");

			for (Long pId : compareWith) {
				couldBe.append(pId + ",");
				if (jobId.compareTo(pId) == 0) {
					return;
				}
			}
			fail(String.format("%s : jobId should be in ( %s) but was %d",
					failMsg, couldBe, jobId));
		} catch (Exception b) {
			b.printStackTrace();
		}
		fail(failMsg + " Exception!  ( " + couldBe + ")");
	}

	/**
	 * Gets the long value of the field named {@code timestamp} in the esper
	 * bean.
	 * 
	 * @param e
	 *            the event bean
	 * @return the timestamp field
	 * @see #getLong(com.espertech.esper.client.EventBean, String)
	 */
	public static Long getTimeStamp(EventBean e) {
		return getLong(e, "timestamp");
	}

	/**
	 * Retrieves the named field and attempts to parse it to a {@link Long}
	 * value.
	 * 
	 * @param e
	 *            the event bean
	 * @param field
	 *            the field to retrieve
	 * @return the value
	 */
	public static Long getLong(EventBean e, String field) {
		try {
			return (Long) e.get(field);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		fail(" Exception! ");
		return null;
	}

	/**
	 * Retrieves the named field and attempts to parse it to a {@link Double}
	 * value.
	 * 
	 * @param e
	 *            the event bean
	 * @param field
	 *            the field to retrieve
	 * @return the value
	 */
	public static Double getDouble(EventBean e, String field) {
		try {
			return (Double) e.get(field);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		fail(" Exception! ");
		return null;
	}

	/**
	 * Checks if the actual value in the range of the expected value.<br/>
	 * This method relies on values in ms which represent times Based on the
	 * inaccuracyPerSecond the range is calculated.
	 * <p/>
	 * Example:
	 * 
	 * <pre>
	 * expected = 100000 (=10sec)
	 * inaccuracyPerSecond = 100 (ms) inaccuracy per second
	 *  => total inaccuracy = 10* 100ms = 1 sec
	 * actual has to be in the range of:
	 * 10sec - 1sec <= actual <= 10sec + 1sec
	 * </pre>
	 * 
	 * @param failMsg
	 *            the msg to show in case of failure
	 * @param expected
	 *            the expected value (in ms)
	 * @param actualValue
	 *            (in ms)
	 * @param inaccuracyPerSecond
	 *            in ms per second
	 * @return
	 */
	public static boolean ensureRange(String failMsg, long expected,
			long actualValue, int inaccuracyPerSecond) {
		return ensureRange(failMsg, expected, actualValue, inaccuracyPerSecond,
				true);
	}

	/**
	 * checks if the actual value in the range of the expected value This method
	 * relies on values in ms which represent times Based on the
	 * inaccuracyPerSecond the range is calculated.
	 * <p/>
	 * Example:
	 * 
	 * <pre>
	 * expected = 100000 (=10sec)
	 * inaccuracyPerSecond = 100 (ms) inaccuracy per second
	 *  => total inaccuracy = 10* 100ms = 1 sec
	 * actual has to be in the range of:
	 * 10sec - 1sec <= actual <= 10sec + 1sec
	 * </pre>
	 * 
	 * @param failMsg
	 *            the error message
	 * @param expected
	 *            the expected value
	 * @param actualValue
	 *            the actual value
	 * @param inaccuracyPerSecond
	 *            the allowed inaccuracy
	 * @param fail
	 *            stop this thread with an Assertion or just print warning to
	 *            stdout
	 * @return true or (false iff fail=true & failed)
	 */
	public static boolean ensureRange(String failMsg, long expected,
			long actualValue, int inaccuracyPerSecond, boolean fail) {
		double inaccuracy = inaccuracyPerSecond * (expected / 1000.0);
		if (inaccuracy < inaccuracyPerSecond) {
			inaccuracy = inaccuracyPerSecond;
		}
		if (expected - inaccuracy <= actualValue
				&& actualValue <= expected + inaccuracy) {
			return true;
		}

		String txt = String.format(
				"Value %d not equal (+/-%s) expected: %d (%s)", actualValue,
				inaccuracy, expected, failMsg);
		if (fail) {
			fail(txt);
		} else {
			System.out.println(txt);
		}
		return false;
	}

	/**
	 * Checks if the given esper bean contains a certain field.
	 * 
	 * @param e
	 *            the esper bean
	 * @param field
	 *            the field to look for
	 * @return {@code true} if the field was found, {@code false} otherwise
	 */
	public static boolean fieldExists(EventBean e, String field) {
		return e.getEventType().isProperty(field);
	}

	/**
	 * Given an event bean, return a list of underlying event beans of the
	 * "top-level" (outermost or leftmost) aliased event declaration. This is
	 * particularly relevant for pattern queries, for example:
	 * "every(...) alias1=EventType1(...) -> ... -> alias2=EventType2(...)".
	 * 
	 * For the example given above, this method would return the list of event
	 * beans that is bound to the alias "alias1" and "alias2".
	 * 
	 * @param e
	 *            the Esper event bean
	 * @return a list of underlying aliased event beans, if present; otherwise
	 *         an empty list.
	 */
	public static List<EventBean> getAliasedEventBeans(EventBean e) {
		List<EventBean> list = new ArrayList<EventBean>();
		if (e instanceof MappedEventBean) {
			Map<String, Object> map = ((MappedEventBean) e).getProperties();
			for (Object value : map.values()) {
				if(value instanceof EventBean)
					list.add((EventBean) value);
			}
		}
		return list;
	}

	/**
	 * Causes the currently executing thread to sleep for the a random amount of
	 * number of milliseconds up to the given maximum duration.<br/>
	 * <p/>
	 * If any thread has interrupted the current thread, the
	 * {@link InterruptedException} is caught silently, the stack trace is
	 * printed to the stand error stream and the method returns immediately.
	 * 
	 * @param maxDuration
	 *            the maximum length of time to sleep in milliseconds
	 * @throws IllegalArgumentException
	 *             if the value of millis is negative
	 */
	public static void sleepRandom(long maxDuration) {
		try {
			Thread.sleep((long) (Math.random() * maxDuration));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
