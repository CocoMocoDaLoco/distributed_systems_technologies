package dst.ass3.event;

import static dst.ass3.EventingUtils.ESPER_CHECK_TIMEOUT;
import static dst.ass3.util.Utils.SHORT_WAIT;
import static dst.ass3.util.Utils.assure;
import static dst.ass3.util.Utils.logCheckpoint;
import static dst.ass3.util.Utils.logTimed;
import static dst.ass3.util.Utils.sleep;
import static org.junit.Assert.assertEquals;

import java.util.List;
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
 * Use pattern matching facilities of EPL to detect tasks which have 3 times
 * attempted and failed to execute (i.e., switched 3 times between the status
 * READY FOR PROCESSING and the status PROCESSING NOT POSSIBLE).
 */
public class Test6 extends AbstractEventTest {

    private Semaphore semSwitch;

    @Test
    public void test_PatternMatchingQuery() {
        final long startTime = System.currentTimeMillis();

        semSwitch = new Semaphore(0);
        test = EventingFactory.getInstance();

        ITask t1 = EventingFactory.createTask(601L, 611L, TaskStatus.ASSIGNED,
                "c1", TaskComplexity.EASY);

        test.initializeAll(new StatementAwareUpdateListener() {

            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents,
                    EPStatement s, EPServiceProvider p) {
                try {
                    for (EventBean e : newEvents) {
                        String name = e.getEventType().getName();
                        if (name.equals(Constants.EVENT_TASK_ASSIGNED)
                                || name.equals(Constants.EVENT_TASK_PROCESSED)
                                || name.equals(Constants.EVENT_TASK_DURATION)) {
                            return;
                        }

                        List<EventBean> eventBeans = EventingUtils
                                .getAliasedEventBeans(e);

                        for (EventBean task : eventBeans) {
                            if (task != null) {
                                TaskStatus status = getTaskStatus(task);

                                if (status == null
                                        || !status
                                                .equals(TaskStatus.READY_FOR_PROCESSING))
                                    continue;

                                System.out.println("job id "
                                        + task.getUnderlying());

                                assertEquals("complexity wrong",
                                        TaskComplexity.EASY,
                                        getTaskComplexity(task));
                                assertEquals("ratedBy wrong", "c1",
                                        task.get("ratedBy").toString());
                                assertEquals("jobId wrong", 611L, EventingUtils
                                        .getLong(task, "jobId").longValue());
                                semSwitch.release();

                                break;
                            }
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, false);

        sleep(SHORT_WAIT); // wait for setup
        logCheckpoint(0, startTime);

        t1.setStatus(TaskStatus.READY_FOR_PROCESSING);
        test.addEvent(t1);

        for (int i = 0; i < 3; i++) {
            t1.setStatus(TaskStatus.PROCESSING_NOT_POSSIBLE);
            test.addEvent(t1);
            sleep(SHORT_WAIT);

            t1.setStatus(TaskStatus.READY_FOR_PROCESSING);
            test.addEvent(t1);
        }

        sleep(SHORT_WAIT); // wait for all events
        logTimed("checking results", startTime);

        assure(semSwitch, 1, "1 switch event expected!", ESPER_CHECK_TIMEOUT);
    }
}
