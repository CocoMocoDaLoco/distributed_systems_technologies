package dst.ass3.jms;

import static dst.ass3.util.Utils.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dst.ass3.AbstractJMSTest;
import dst.ass3.dto.ProcessTaskDTO;
import dst.ass3.dto.RateTaskDTO;
import dst.ass3.dto.TaskDTO;
import dst.ass3.jms.cluster.ICluster.IClusterListener;
import dst.ass3.jms.computer.IComputer.IComputerListener;
import dst.ass3.jms.scheduler.IScheduler.ISchedulerListener;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

/**
 * This test performs the following tasks: Assign 3 Tasks, accept and process
 * all of them.
 * 
 * <pre>
 * Timing diagram
 * 
 *      0  1  2  3  4  5  6  7  8  9  10 [sec]                                     
 *      |--|--|--|--|--|--|--|--|--|--|-->                                         
 * J1/2 ******************                                                         
 * J3         ******************                                                   
 *      ^     ^     ^        ^        ^                                            
 *      CP1   CP2   CP3      CP4      CP5                                           
 *                                                                                 
 * Each Job needs ****************** (=6sec) for processing                        
 * CP1: Check-Point 1 - Assign Job1, Job2                                                           
 * CP2: Check-Point 2 - Assign Job3                                                                 
 * CP3: Check-Point 3 - 1-3 running                                                                 
 * CP4: Check-Point 4 - 1-2 PROCESSED, 3 running                                                    
 * CP5: Check-Point 5 - 3 PROCESSED
 * </pre>
 */
public class Test7 extends AbstractJMSTest {

    private AtomicInteger clusterEventJob1 = new AtomicInteger(0);
    private AtomicInteger clusterEventJob2 = new AtomicInteger(0);
    private AtomicInteger clusterEventJob3 = new AtomicInteger(0);

    private AtomicInteger schedulerEvent = new AtomicInteger(0);
    private AtomicInteger schedulerEventJob1 = new AtomicInteger(0);
    private AtomicInteger schedulerEventJob2 = new AtomicInteger(0);
    private AtomicInteger schedulerEventJob3 = new AtomicInteger(0);

    private AtomicInteger computerEvent = new AtomicInteger(0);
    private AtomicInteger computerEventJob1 = new AtomicInteger(0);
    private AtomicInteger computerEventJob2 = new AtomicInteger(0);
    private AtomicInteger computerEventJob3 = new AtomicInteger(0);

    private long jobId1 = 70;
    private long jobId2 = 71;
    private long jobId3 = 72;
    private long taskId1 = 0;
    private long taskId2 = 0;
    private long taskId3 = 0;

    private String task1RatedBy;
    private String task2RatedBy;
    private String task3RatedBy;
    private long startTime;

    private final int PROCESSING_TIME = 6000;

    private Semaphore sem;
    private Semaphore semComputer;

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void test_ComplexAssignRateAndProcess() {
        sem = new Semaphore(0);
        semComputer = new Semaphore(0);

        c1.start();
        c2.start();
        scheduler.start();
        pc1.start();
        pc2.start();
        pc3.start();
        pc4.start();

        IClusterListener clusterListener = new IClusterListener() {
            @Override
            public TaskDecideResponse decideTask(RateTaskDTO task,
                    String clusterName) {
                logTimed("** cluster " + clusterName + " task: " + task,
                        startTime);
                if (task.getJobId().longValue() == jobId1) {
                    clusterEventJob1.incrementAndGet();
                    taskId1 = task.getId();
                    task1RatedBy = clusterName;
                    return new TaskDecideResponse(TaskResponse.ACCEPT,
                            TaskComplexity.EASY);
                }
                if (task.getJobId().longValue() == jobId2) {
                    clusterEventJob2.incrementAndGet();
                    taskId2 = task.getId();
                    task2RatedBy = clusterName;
                    return new TaskDecideResponse(TaskResponse.ACCEPT,
                            TaskComplexity.CHALLENGING);
                }
                if (task.getJobId().longValue() == jobId3) {
                    clusterEventJob3.incrementAndGet();
                    taskId3 = task.getId();
                    task3RatedBy = clusterName;
                    return new TaskDecideResponse(TaskResponse.ACCEPT,
                            TaskComplexity.CHALLENGING);
                }

                fail("cluster Events - unknown type");
                return new TaskDecideResponse(TaskResponse.DENY, null);
            }
        };

        IComputerListener computerListener = new IComputerListener() {
            @Override
            public void waitTillProcessed(ProcessTaskDTO task,
                    String computerName, TaskComplexity acceptedComplexity,
                    String clusterName) {
                logTimed("** computer " + computerName + " task: " + task,
                        startTime);

                if (task.getId().longValue() == taskId1) {
                    computerEventJob1.incrementAndGet();
                    assertEquals("computerListener 1 taskId", taskId1, task
                            .getId().longValue());
                    assertEquals("computerListener 1 jobId", jobId1, task
                            .getJobId().longValue());
                    assertEquals("computerListener 1 complexity",
                            TaskComplexity.EASY, task.getComplexity());
                    assertEquals("computerListener 1 ratedby", task1RatedBy,
                            task.getRatedBy());
                }
                if (task.getId().longValue() == taskId2) {
                    computerEventJob2.incrementAndGet();
                    assertEquals("computerListener 2 taskId", taskId2, task
                            .getId().longValue());
                    assertEquals("computerListener 2 jobId", jobId2, task
                            .getJobId().longValue());
                    assertEquals("computerListener 2 complexity",
                            TaskComplexity.CHALLENGING, task.getComplexity());
                    assertEquals("computerListener 2 ratedby", task2RatedBy,
                            task.getRatedBy());
                }
                if (task.getId().longValue() == taskId3) {
                    computerEventJob3.incrementAndGet();
                    assertEquals("computerListener 3 taskId", taskId3, task
                            .getId().longValue());
                    assertEquals("computerListener 3 jobId", jobId3, task
                            .getJobId().longValue());
                    assertEquals("computerListener 3 complexity",
                            TaskComplexity.CHALLENGING, task.getComplexity());
                    assertEquals("computerListener 3 ratedby", task3RatedBy,
                            task.getRatedBy());
                }

                computerEvent.incrementAndGet();
                sem.release();

                sleep(PROCESSING_TIME);

                semComputer.release();
                logTimed("finish " + computerName, startTime);
            }
        };

        ISchedulerListener schedulerListener = new ISchedulerListener() {
            @Override
            public void notify(InfoType type, TaskDTO task) {
                logTimed("** scheduler: type=" + type + " task: " + task,
                        startTime);
                sleep(SHORT_WAIT); // wait short time for updated taskId
                if (task.getJobId().longValue() == jobId1) {
                    assertEquals("taskId in server response DTO wrong"
                            + schedulerEvent, taskId1, task.getId().longValue());
                    schedulerEventJob1.incrementAndGet();
                }
                if (task.getJobId().longValue() == jobId2) {
                    assertEquals("taskId in server response DTO wrong Job2 "
                            + schedulerEvent, taskId2, task.getId().longValue());
                    schedulerEventJob2.incrementAndGet();
                }
                if (task.getJobId().longValue() == jobId3) {
                    assertEquals("taskId in server response DTO wrong Job3 "
                            + schedulerEvent, taskId3, task.getId().longValue());
                    schedulerEventJob3.incrementAndGet();
                }

                switch (schedulerEvent.get()) {
                case 0:
                    // ASSIGN for jobId1 / jobId2
                    assertEquals("1st event of wrong type", InfoType.CREATED,
                            type);
                    assertEquals("1st event wrong", TaskStatus.ASSIGNED,
                            task.getStatus());
                    assertEquals("1st event complexity wrong",
                            TaskComplexity.UNRATED, task.getComplexity());
                    break;
                case 1:
                    // ASSIGN for jobId1 / jobId2
                    assertEquals("2nd event of wrong type", InfoType.CREATED,
                            type);
                    assertEquals("2nd event wrong", TaskStatus.ASSIGNED,
                            task.getStatus());
                    assertEquals("2nd event complexity wrong",
                            TaskComplexity.UNRATED, task.getComplexity());
                    break;
                case 2:
                    // ASSIGN for jobId3
                    assertEquals("3rd event of wrong type", InfoType.CREATED,
                            type);
                    assertEquals("3rd event wrong", TaskStatus.ASSIGNED,
                            task.getStatus());
                    assertEquals("3rd event complexity wrong",
                            TaskComplexity.UNRATED, task.getComplexity());
                    break;
                case 3:
                    // PROCESSED 1-2
                    assertEquals("4th event of wrong type", InfoType.PROCESSED,
                            type);
                    assertEquals("4th event wrong", TaskStatus.PROCESSED,
                            task.getStatus());
                    assertNotSame("4th event complexity wrong",
                            TaskComplexity.UNRATED, task.getComplexity());
                    break;
                case 4:
                    // PROCESSED 1-2
                    assertEquals("5th event of wrong type", InfoType.PROCESSED,
                            type);
                    assertEquals("5th event wrong", TaskStatus.PROCESSED,
                            task.getStatus());
                    assertNotSame("5th event complexity wrong",
                            TaskComplexity.UNRATED, task.getComplexity());
                    break;
                case 5:
                    // PROCESSED 3
                    assertEquals("6th event of wrong type", InfoType.PROCESSED,
                            type);
                    assertEquals("6th wrong task", jobId3, task.getJobId()
                            .longValue());
                    assertEquals("6th event wrong", TaskStatus.PROCESSED,
                            task.getStatus());
                    assertEquals("6th event complexity ",
                            TaskComplexity.CHALLENGING, task.getComplexity());
                    assertEquals("6th event rated by", task3RatedBy,
                            task.getRatedBy());
                    break;
                default:
                    fail("only 6 events expected");
                    break;
                }

                schedulerEvent.incrementAndGet();

                sem.release();
            }
        };

        sleep(SHORT_WAIT); // Wait for old messages being discarded.

        startTime = new Date().getTime();
        // ---------------- CP1 ------------------------
        logCheckpoint(1, startTime);

        c1.setClusterListener(clusterListener);
        c2.setClusterListener(clusterListener);

        pc1.setComputerListener(computerListener);
        pc2.setComputerListener(computerListener);
        pc3.setComputerListener(computerListener);
        pc4.setComputerListener(computerListener);

        scheduler.setSchedulerListener(schedulerListener);

        log("Assigning " + jobId1 + "...");
        scheduler.assign(jobId1);
        log("Assigning " + jobId2 + "...");
        scheduler.assign(jobId2);

        assure(sem,
                4,
                "did not receive 4 events (Scheduler: create, create, Computer: pre-processing, pre-processing) in time",
                DEFAULT_CHECK_TIMEOUT);

        // ---------------- CP2 ------------------------
        logCheckpoint(2, startTime);

        assertEquals("wrong count of scheduler events ", 2,
                schedulerEvent.get());
        assertEquals("wrong count of scheduler 1 events ", 1,
                schedulerEventJob1.get());
        assertEquals("wrong count of scheduler 2 events ", 1,
                schedulerEventJob2.get());
        assertEquals("wrong count of computer 1 events ", 1,
                computerEventJob1.get());
        assertEquals("wrong count of computer 2 events ", 1,
                computerEventJob2.get());
        assertEquals("wrong count of computer 3 events ", 0,
                computerEventJob3.get());

        sleep(2000); // 2 sec delayed startup
        scheduler.assign(jobId3);

        assure(sem, 2, "did not receive 2 event (Scheduler: create, Computer: pre-processing) in time",
                DEFAULT_CHECK_TIMEOUT);

        // ---------------- CP3 ------------------------
        logCheckpoint(3, startTime);

        assertEquals("wrong count of computer events ", 3, computerEvent.get());

        assertEquals("wrong count of scheduler events ", 3,
                schedulerEvent.get());
        assertEquals("wrong count of scheduler 1 events ", 1,
                schedulerEventJob1.get());
        assertEquals("wrong count of scheduler 2 events ", 1,
                schedulerEventJob2.get());
        assertEquals("wrong count of scheduler 3 events ", 1,
                schedulerEventJob3.get());
        assertEquals("wrong count of computer 1 events ", 1,
                computerEventJob1.get());
        assertEquals("wrong count of computer 2 events ", 1,
                computerEventJob2.get());
        assertEquals("wrong count of computer 3 events ", 1,
                computerEventJob3.get());

        // Job 1 and 2 need some time to finish
        assure(semComputer,
                2,
                "did not receive 2 events (Computer: finished processing, finished processing) in time",
                DEFAULT_CHECK_TIMEOUT + PROCESSING_TIME / 1000);
        assure(sem,
                2,
                "did not receive 2 events (Scheduler: processed, processed) in time",
                DEFAULT_CHECK_TIMEOUT);

        // ---------------- CP4 ------------------------
        logCheckpoint(4, startTime);

        assertEquals("wrong count of scheduler events ", 5,
                schedulerEvent.get());
        assertEquals("wrong count of scheduler 1 events ", 2,
                schedulerEventJob1.get());
        assertEquals("wrong count of scheduler 2 events ", 2,
                schedulerEventJob2.get());
        assertEquals("wrong count of scheduler 3 events ", 1,
                schedulerEventJob3.get());

        assertEquals("wrong count of cluster Job1 events ", 1,
                clusterEventJob1.get());
        assertEquals("wrong count of cluster Job2 events ", 1,
                clusterEventJob2.get());
        assertEquals("wrong count of cluster Job3 events ", 1,
                clusterEventJob3.get());

        assertEquals("wrong count of computer events ", 3, computerEvent.get());
        assertEquals("wrong count of computer 1 events ", 1,
                computerEventJob1.get());
        assertEquals("wrong count of computer 2 events ", 1,
                computerEventJob2.get());
        assertEquals("wrong count of computer 3 events ", 1,
                computerEventJob3.get());

        assure(semComputer,
                1,
                "did not receive 1 event (Computer: finished processing) in time",
                DEFAULT_CHECK_TIMEOUT + PROCESSING_TIME / 1000);

        assure(sem, 1,
                "did not receive 1 event (Scheduler: processed) in time",
                DEFAULT_CHECK_TIMEOUT);

        // ---------------- CP5 ------------------------
        logCheckpoint(5, startTime);
        assertEquals("wrong count of scheduler events ", 6,
                schedulerEvent.intValue());
        assertEquals("wrong count of scheduler 1 events ", 2,
                schedulerEventJob1.get());
        assertEquals("wrong count of scheduler 2 events ", 2,
                schedulerEventJob2.get());
        assertEquals("wrong count of scheduler 3 events ", 2,
                schedulerEventJob3.get());

        assertEquals("wrong count of cluster Job1 events ", 1,
                clusterEventJob1.get());
        assertEquals("wrong count of cluster Job2 events ", 1,
                clusterEventJob2.get());
        assertEquals("wrong count of cluster Job3 events ", 1,
                clusterEventJob3.get());

        assertEquals("wrong count of computer events ", 3, computerEvent.get());
        assertEquals("wrong count of computer 1 events ", 1,
                computerEventJob1.get());
        assertEquals("wrong count of computer 2 events ", 1,
                computerEventJob2.get());
        assertEquals("wrong count of computer 3 events ", 1,
                computerEventJob3.get());

    }

    @After
    public void shutdown() {
        // disable all listeners
        c1.setClusterListener(null);
        c2.setClusterListener(null);
        scheduler.setSchedulerListener(null);

        log("shutting down pc1,pc2,pc3,pc4...");
        pc1.stop();
        pc2.stop();
        pc3.stop();
        pc4.stop();
        log("shutting down c1...");
        c1.stop();
        log("shutting down c2...");
        c2.stop();
        log("shutting down S...");
        scheduler.stop();

    }

}
