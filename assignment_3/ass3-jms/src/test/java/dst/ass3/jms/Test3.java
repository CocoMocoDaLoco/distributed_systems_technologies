package dst.ass3.jms;

import static dst.ass3.util.Utils.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dst.ass3.AbstractJMSTest;
import dst.ass3.dto.RateTaskDTO;
import dst.ass3.dto.TaskDTO;
import dst.ass3.jms.cluster.ICluster.IClusterListener;
import dst.ass3.jms.scheduler.IScheduler.ISchedulerListener;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

/**
 * This test performs the following tasks: ASSIGN 1 task, RATE it as EASY and
 * perform INFO 2 times.
 * 
 * <pre>
 * Timing diagram
 * 
 *    0  1  2  3  4 [sec]                                                                 
 *    |--|--|--|--|-->                                                                    
 * J1 ###################...                                                              
 * I1       *                                                                             
 * I2       *                                                                             
 *    ^     ^     ^                                                                       
 *    CP1   CP2   CP3                                                                      
 *                                                                                        
 * ##: waiting for processing (no computer listener = computer discards message)
 * 
 * J1: Job1
 * I1,I2: Info Request 1,2
 * CP1: Check-Point 1 - assign Job1, Cluster accepts as EASY                                               
 * CP2: Check-Point 2 - request info 2x [Scheduler = ASSIGN]                                               
 * CP3: Check-Point 3 - info finished  [Scheduler = INFO, INFO]
 * </pre>
 */
public class Test3 extends AbstractJMSTest {

    private AtomicInteger clusterEvent = new AtomicInteger(0);
    private AtomicInteger schedulerEvent = new AtomicInteger(0);

    private long jobId1 = 30;
    private long taskId1 = 0;

    private String ratedBy = null;
    private long startTime;

    private Semaphore sem;

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void test_AssignRateAndInfo() {
        sem = new Semaphore(0);

        c1.start();
        c2.start();
        scheduler.start();
        pc2.start();
        pc4.start();

        IClusterListener clusterListener1 = new IClusterListener() {
            @Override
            public TaskDecideResponse decideTask(RateTaskDTO task,
                    String clusterName) {
                logTimed("** cluster " + clusterName + " task: " + task,
                        startTime);

                clusterEvent.incrementAndGet();

                assertNotNull("task.jobId = null", task.getJobId());
                assertNotNull("task.taskId = null", task.getId());

                assertEquals("jobId wrong", jobId1, task.getJobId().longValue());

                log("SETTING ID " + task.getId());
                taskId1 = task.getId();

                ratedBy = "c1";
                assertEquals("reported clustername wrong", ratedBy, clusterName);

                sem.release();
                return new TaskDecideResponse(TaskResponse.ACCEPT,
                        TaskComplexity.EASY);
            }
        };

        IClusterListener clusterListener2 = new IClusterListener() {
            @Override
            public TaskDecideResponse decideTask(RateTaskDTO task,
                    String clusterName) {
                logTimed("** cluster " + clusterName + " task: " + task,
                        startTime);

                clusterEvent.incrementAndGet();

                assertNotNull("task.jobId = null", task.getJobId());
                assertNotNull("task.taskId = null", task.getId());

                assertEquals("jobId wrong", jobId1, task.getJobId().longValue());

                log("SETTING ID " + task.getId());
                taskId1 = task.getId();

                ratedBy = "c2";
                assertEquals("reported clustername wrong", ratedBy, clusterName);

                sem.release();
                return new TaskDecideResponse(TaskResponse.ACCEPT,
                        TaskComplexity.EASY);
            }
        };

        ISchedulerListener schedulerListener = new ISchedulerListener() {
            @Override
            public void notify(InfoType type, TaskDTO task) {
                logTimed("** scheduler: type=" + type + " task: " + task,
                        startTime);

                sleep(SHORT_WAIT); // wait short time for updated taskId

                assertEquals("jobId in server response DTO wrong " + jobId1,
                        jobId1, task.getJobId().longValue());
                assertEquals("taskId in server response DTO wrong"
                        + schedulerEvent, taskId1, task.getId().longValue());

                switch (schedulerEvent.get()) {
                case 0:
                    // ASSIGN for jobId1
                    assertEquals("1st event of wrong type", InfoType.CREATED,
                            type);
                    assertEquals("1st event != ASSIGNED", TaskStatus.ASSIGNED,
                            task.getStatus());
                    assertEquals("1st event complexity != UNRATED",
                            TaskComplexity.UNRATED, task.getComplexity());
                    break;
                case 1:
                    // INFO 1
                    assertEquals("2nd event of wrong type", InfoType.INFO, type);
                    assertEquals("2nd event != ASSIGNED",
                            TaskStatus.READY_FOR_PROCESSING, task.getStatus());
                    assertEquals("2nd event complexity != EASY",
                            TaskComplexity.EASY, task.getComplexity());
                    assertNotNull("2nd rated by == null" + ratedBy,
                            task.getRatedBy());
                    assertEquals("2nd rated by != " + ratedBy, ratedBy,
                            task.getRatedBy());
                    break;
                case 2:
                    // INFO 2
                    assertEquals("3rd event of wrong type", InfoType.INFO, type);
                    assertEquals("3rd event != ASSIGNED",
                            TaskStatus.READY_FOR_PROCESSING, task.getStatus());
                    assertEquals("3rd event complexity != EASY",
                            TaskComplexity.EASY, task.getComplexity());
                    assertNotNull("3rd rated by == null" + ratedBy,
                            task.getRatedBy());
                    assertEquals("3rd rated by != " + ratedBy, ratedBy,
                            task.getRatedBy());
                    break;
                default:
                    fail("only 3 events expected");
                    break;
                }
                sem.release();
                schedulerEvent.incrementAndGet();
            }
        };

        sleep(SHORT_WAIT); // Wait for old messages being discarded.
        startTime = new Date().getTime();

        // ---------------- CP1 ------------------------
        logCheckpoint(1, startTime);

        c1.setClusterListener(clusterListener1);
        c2.setClusterListener(clusterListener2);
        scheduler.setSchedulerListener(schedulerListener);

        log("Assigning " + jobId1 + "...");
        scheduler.assign(jobId1);

        // ---------------- CP2 ------------------------
        logCheckpoint(2, startTime);
        assure(sem,
                2,
                "did not get 2 events (Scheduler: assign, Cluster: rate) in time",
                DEFAULT_CHECK_TIMEOUT);

        assertEquals("wrong count of cluster events ", 1, clusterEvent.get());
        assertEquals("wrong count of scheduler events ", 1,
                schedulerEvent.get());

        log("Executing info 2x " + taskId1 + "...");
        scheduler.info(taskId1);
        scheduler.info(taskId1);

        // ---------------- CP3 ------------------------
        logCheckpoint(3, startTime);
        assure(sem, 2, "did not get 2 events (Scheduler: info, info) in time",
                DEFAULT_CHECK_TIMEOUT);

        assertEquals("wrong count of scheduler events ", 3,
                schedulerEvent.get());
        assertEquals("wrong count of cluster events ", 1, clusterEvent.get());
    }

    @After
    public void shutdown() {
        // disable all listeners
        c1.setClusterListener(null);
        c2.setClusterListener(null);
        scheduler.setSchedulerListener(null);

        log("shutting down c1...");
        c1.stop();
        log("shutting down c2...");
        c2.stop();
        log("shutting down pc2,4...");
        pc2.stop();
        pc4.stop();
        log("shutting down S...");
        scheduler.stop();
    }

}
