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
 * Basic Test: Adds an ASSIGNED task, changes the Task to Processed and waits
 * for a TaskDuration Event.
 * 
 */
public class Test3 extends AbstractEventTest {

    private Semaphore semDuration;
    private Semaphore semAss;
    private Semaphore semProcessed;

    private long taskId1 = 3L;
    private long taskId2 = 6L;

    private long jobId1 = 5L;
    private long jobId2 = 7L;

    @Test
    public void test_TaskDurationEvent1() {
        final long startTime = System.currentTimeMillis();
        semDuration = new Semaphore(0);
        semAss = new Semaphore(0);
        semProcessed = new Semaphore(0);

        ITask t1 = EventingFactory.createTask(taskId1, jobId1,
                TaskStatus.ASSIGNED, "c1", TaskComplexity.EASY);
        ITask t2 = EventingFactory.createTask(taskId2, jobId2,
                TaskStatus.ASSIGNED, "c1", TaskComplexity.EASY);

        test.initializeAll(new StatementAwareUpdateListener() {

            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents,
                    EPStatement s, EPServiceProvider p) {
                Long timestamp = null;
                for (EventBean e : newEvents) {
                    System.out.println("LISTENER:" + e.getEventType().getName()
                            + " " + e.getUnderlying());
                    String name = e.getEventType().getName();

                    if (name.equals(Constants.EVENT_TASK_DURATION)) {
                        EventingUtils.ensureJobId("Duration", e, jobId1);
                        semDuration.release();
                    } else if (name.equals(Constants.EVENT_TASK_ASSIGNED)) {
                        timestamp = EventingUtils.getTimeStamp(e);
                        EventingUtils
                                .ensureJobId("Assigned", e, jobId1, jobId2);
                        if (timestamp < startTime) {
                            fail("starttime > timestamp (start time:"
                                    + startTime + ", timestamp:" + timestamp
                                    + ")");
                        }
                        semAss.release();
                    } else if (name.equals(Constants.EVENT_TASK_PROCESSED)) {
                        timestamp = EventingUtils.getTimeStamp(e);
                        EventingUtils.ensureJobId(
                                Constants.EVENT_TASK_PROCESSED, e, jobId1);
                        if (timestamp < startTime) {
                            fail("starttime > timestamp (start time:"
                                    + startTime + ", timestamp:" + timestamp
                                    + ")");
                        }
                        semProcessed.release();
                    }

                }

            }
        }, false);

        sleep(SHORT_WAIT); // wait for setup
        logCheckpoint(0, startTime);

        test.addEvent(t1);
        test.addEvent(t2);

        logCheckpoint(1, startTime);
        t1.setStatus(TaskStatus.PROCESSED);
        test.addEvent(t1);

        logTimed("checking results", startTime);
        assure(semDuration, 1, "1 taskDuration event expected!",
                ESPER_CHECK_TIMEOUT);
        assure(semAss, 2, "2 taskAssigned events expected!",
                ESPER_CHECK_TIMEOUT);
        assure(semProcessed, 1, "1 taskProcessed event expected!",
                ESPER_CHECK_TIMEOUT);

    }
}
