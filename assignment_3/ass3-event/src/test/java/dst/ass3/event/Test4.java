package dst.ass3.event;

import static dst.ass3.EventingUtils.ESPER_CHECK_TIMEOUT;
import static dst.ass3.util.Utils.SHORT_WAIT;
import static dst.ass3.util.Utils.assure;
import static dst.ass3.util.Utils.logCheckpoint;
import static dst.ass3.util.Utils.logTimed;
import static dst.ass3.util.Utils.sleep;
import static org.junit.Assert.fail;

import java.util.concurrent.Semaphore;

import org.junit.Test;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

import dst.ass3.AbstractEventTest;
import dst.ass3.EventingUtils;
import dst.ass3.model.ITask;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

/**
 * Checks TaskDuration query.
 * 
 * Timing:
 * <pre>
 *  2s   2s  2s  2s
 * |---|---|---|---|---|---|
 * ^   ^   ^       ^
 * t1  t3  t1      t2
 * t2
 * t3
 * t4
 * </pre>
 */
public class Test4 extends AbstractEventTest {

	private Semaphore semDuration;

	private long[] taskId = {401L,402L,403L,404L};
	private long[] jobIds = {411,412,413,414};

	@Test
	public void test_TaskDurationQuery() {

		semDuration = new Semaphore(0);

		ITask t0 = EventingFactory.createTask(taskId[0], jobIds[0], TaskStatus.ASSIGNED, "c1", TaskComplexity.EASY);
		ITask t1 = EventingFactory.createTask(taskId[1], jobIds[1], TaskStatus.ASSIGNED, "c1", TaskComplexity.EASY);
		ITask t2 = EventingFactory.createTask(taskId[2], jobIds[2], TaskStatus.ASSIGNED, "c1", TaskComplexity.EASY);
		ITask t3 = EventingFactory.createTask(taskId[3], jobIds[3], TaskStatus.ASSIGNED, "c1", TaskComplexity.EASY);

		test.initializeAll(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents,
							   EPStatement s, EPServiceProvider p) {
				Long duration = null;
				for (EventBean e : newEvents) {
					System.out.println("LISTENER:"+ e.getEventType().getName() + " " + e.getUnderlying());
					String name = e.getEventType().getName();
					if (name.equals(Constants.EVENT_TASK_DURATION)) {
						EventingUtils.ensureJobId("Duration", e, jobIds);
						duration = EventingUtils.getLong(e, "duration");
						long jobId = EventingUtils.getLong(e, "jobId");

						if(jobId == jobIds[0]) {
							EventingUtils.ensureRange("Duration " + jobId, 4000, duration, allowedInaccuracy);
							semDuration.release();
						} else if(jobId == jobIds[1]) {
							EventingUtils.ensureRange("Duration " + jobId, 8000, duration, allowedInaccuracy);
							semDuration.release();
						} else if(jobId == jobIds[2]) {
							EventingUtils.ensureRange("Duration " + jobId, 2000, duration, allowedInaccuracy);
							semDuration.release();
						} else {
							fail("unknown jobId! Not expected " + jobId);
						}

					}
				}

			}
		},false);

		sleep(SHORT_WAIT); // wait for setup
		final long startTime = System.currentTimeMillis();
		logCheckpoint(0, startTime);

		test.addEvent(t0);
		test.addEvent(t1);
		test.addEvent(t2);
		test.addEvent(t3);

		sleep(2000); //fixed 2sec see description
		logCheckpoint(1, startTime);

		t2.setStatus(TaskStatus.PROCESSED);
		test.addEvent(t2);

		sleep(2000); //fixed 2sec see description
		logCheckpoint(2, startTime);

		t0.setStatus(TaskStatus.PROCESSED);
		test.addEvent(t0);

		sleep(2000); //fixed 2sec see description
		sleep(2000); //fixed 2sec see description
		logCheckpoint(3, startTime);

		t1.setStatus(TaskStatus.PROCESSED);
		test.addEvent(t1);

		//t4 never reaches processed

		logTimed("checking results", startTime);
		assure(semDuration, 3, "3 taskDuration event expected", ESPER_CHECK_TIMEOUT);

	}
}
