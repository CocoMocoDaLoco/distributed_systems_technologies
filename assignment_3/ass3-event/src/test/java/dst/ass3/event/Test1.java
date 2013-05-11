package dst.ass3.event;

import static dst.ass3.EventingUtils.ESPER_CHECK_TIMEOUT;
import static dst.ass3.util.Utils.SHORT_WAIT;
import static dst.ass3.util.Utils.assure;
import static dst.ass3.util.Utils.logCheckpoint;
import static dst.ass3.util.Utils.logTimed;
import static dst.ass3.util.Utils.sleep;

import java.util.concurrent.Semaphore;

import org.junit.Test;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

import dst.ass3.AbstractEventTest;
import dst.ass3.model.ITask;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

/**
 * Basic Test: Adds an ASSIGNED task and then changes the Task to Processed and
 * waits for a TaskDuration Event.
 * 
 */
public class Test1 extends AbstractEventTest {

    private Semaphore sem;

    @Test
    public void test_TaskDurationEvent() {
        final long startTime = System.currentTimeMillis();
        sem = new Semaphore(0);

        ITask t1 = EventingFactory.createTask(101L, 111L, TaskStatus.ASSIGNED,
                "c1", TaskComplexity.EASY);

        test.initializeAll(new StatementAwareUpdateListener() {

            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents,
                    EPStatement s, EPServiceProvider p) {

                for (EventBean e : newEvents) {
                    String name = e.getEventType().getName();
                    if (name.equals(Constants.EVENT_TASK_DURATION)) {
                        sem.release();
                    }
                    System.out.println("LISTENER " + e.getEventType().getName()
                            + " " + e.getUnderlying());
                }

            }
        }, false);

        sleep(SHORT_WAIT);
        logCheckpoint(0, startTime);
        test.addEvent(t1);

        logCheckpoint(1, startTime);
        t1.setStatus(TaskStatus.PROCESSED);
        test.addEvent(t1);

        logTimed("checking results", startTime);
        assure(sem, 1, "1 taskDuration event expected!", ESPER_CHECK_TIMEOUT);

    }
}
