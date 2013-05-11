package dst.ass3.jms;

import static dst.ass3.jms.cluster.ICluster.IClusterListener;
import static dst.ass3.jms.computer.IComputer.IComputerListener;
import static dst.ass3.jms.scheduler.IScheduler.ISchedulerListener;
import static dst.ass3.util.Utils.*;
import static org.junit.Assert.*;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import dst.ass3.AbstractJMSTest;
import dst.ass3.dto.ProcessTaskDTO;
import dst.ass3.dto.RateTaskDTO;
import dst.ass3.dto.TaskDTO;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * This test performs the following tasks: ASSIGN 1 task, RATE it as CHALLENGING
 * and PROCESS it.
 * 
 * <pre>
 * Timing diagram
 * 
 *    0  1  2  3  4                                                                       
 *    |--|--|--|--|-->                                                                    
 * J1 *******                                                                             
 * I1          *                                                                           
 *    ^     ^  ^  ^                                                                       
 *    CP1   CP2+3 CP4                                                                      
 *                                                                                        
 * **: Running                                                                            
 *                                                                                        
 * J1: takes 2sec to finish                                                               
 * I1: InfoRequest for Task1                                                              
 *                                                                                        
 * CP1: Check-Point 1 - Assign Job 1                                         
 * CP2: Check-Point 2 - Wait till Job 1 has finished                         
 * CP3: Check-Point 3 - Job1 should have finished so send INFO Request                                                    
 * CP4: Check-Point 4 - Info Request completed [Scheduler = ASSIGN, PROCESSED, INFO events]
 * </pre>
 */
public class Test4 extends AbstractJMSTest {

    private AtomicInteger clusterEvent = new AtomicInteger(0);
    private AtomicInteger schedulerEvent = new AtomicInteger(0);
    private AtomicInteger processed = new AtomicInteger(0);

    private long jobId1 = 40;
    private long taskId1 = 0;

    private String ratedBy = null;
    private long startTime;

    private Semaphore sem;
    private Semaphore semComputer;

    private final int PROCESSING_TIME = 2000;

    @Before
    public void init() {
        super.init();
    }

    @Test
    public void test_AssignRateAndProcess1() {
        sem = new Semaphore(0);
        semComputer = new Semaphore(0);
        c1.start();
        scheduler.start();
        pc1.start();

        IClusterListener clusterListener = new IClusterListener() {
            @Override
            public TaskDecideResponse decideTask(RateTaskDTO task,
                    String clusterName) {
                logTimed("** cluster " + clusterName + " task: " + task,
                        startTime);

                assertNotNull("task.jobId = null", task.getJobId());
                assertNotNull("task.taskId = null", task.getId());
                
                assertEquals("jobId wrong", jobId1, task.getJobId().longValue());

                log("SETTING ID " + task.getId());
                taskId1 = task.getId();
                ratedBy = clusterName;

                clusterEvent.incrementAndGet();
                sem.release();

                return new TaskDecideResponse(TaskResponse.ACCEPT,
                        TaskComplexity.CHALLENGING);
            }
        };

        IComputerListener computerListener = new IComputerListener() {
            @Override
            public void waitTillProcessed(ProcessTaskDTO task,
                    String computerName, TaskComplexity acceptedComplexity,
                    String clusterName) {
                logTimed("** computer " + computerName + " task: " + task,
                        startTime);

                assertEquals("clusterName", ratedBy, clusterName);
                assertEquals("computerName", "pc1", computerName);
                assertEquals("taskcomplexity", TaskComplexity.CHALLENGING,
                        acceptedComplexity);

                sleep(PROCESSING_TIME); // simulate processing

                processed.incrementAndGet();

                semComputer.release();
                assertEquals("computer listener - too many events", 1,
                        processed.get());
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
                    assertEquals("1st event != ASSIGNED",
                            TaskStatus.ASSIGNED, task.getStatus());
                    assertEquals("1st event complexity != UNRATED",
                            TaskComplexity.UNRATED, task.getComplexity());
                    break;
                case 1:
                    // PROCESSED
                    assertEquals("2nd event of wrong type", InfoType.PROCESSED,
                            type);
                    assertEquals("2nd event != ASSIGNED", TaskStatus.PROCESSED,
                            task.getStatus());
                    assertEquals("2nd event complexity ",
                            TaskComplexity.CHALLENGING, task.getComplexity());
                    assertNotNull("2nd rated by == null " + ratedBy,
                            task.getRatedBy());
                    assertEquals("2nd rated by != " + ratedBy, ratedBy,
                            task.getRatedBy());
                    break;
                case 2:
                    // INFO
                    assertEquals("3rd event of wrong type", InfoType.INFO, type);
                    assertEquals("3rd event != ASSIGNED", TaskStatus.PROCESSED,
                            task.getStatus());
                    assertEquals("3rd event complexity ",
                            TaskComplexity.CHALLENGING, task.getComplexity());
                    assertNotNull("3rd rated by == null" + ratedBy,
                            task.getRatedBy());
                    assertEquals("3rd rated by != " + ratedBy, ratedBy,
                            task.getRatedBy());
                    break;
                default:
                    fail("only 3 events expected");
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
        scheduler.setSchedulerListener(schedulerListener);
        pc1.setComputerListener(computerListener);

        log("Assigning " + jobId1 + "...");
        scheduler.assign(jobId1);

        assure(sem,
                2,
                "did not get 2 events (Scheduler: create; Cluster: rate) in time",
                DEFAULT_CHECK_TIMEOUT);
        // ---------------- CP2 ------------------------
        logCheckpoint(2, startTime);

        assertEquals("wrong count of cluster events ", 1, clusterEvent.get());
        assertEquals("wrong count of scheduler events ", 1,
                schedulerEvent.get());

        assure(semComputer, 1,
                "did not get 1 event (Computer: finished processing) in time",
                DEFAULT_CHECK_TIMEOUT + PROCESSING_TIME / 1000);
        assure(sem, 1, "did not get 1 event (Scheduler: processed) in time",
                DEFAULT_CHECK_TIMEOUT);

        // ---------------- CP3 ------------------------
        logCheckpoint(3, startTime);
        assertEquals("wrong count of computer events", 1, processed.get());
        assertEquals("wrong count of scheduler events ", 2,
                schedulerEvent.get());
        assertEquals("wrong count of cluster events ", 1, clusterEvent.get());

        log("Executing info " + taskId1 + "...");
        scheduler.info(taskId1);

        assure(sem, 1, "did not get 1 event (Scheduler: info) in time",
                DEFAULT_CHECK_TIMEOUT);

        // ---------------- CP4 ------------------------
        logCheckpoint(4, startTime);

        assertEquals("wrong count of scheduler events ", 3,
                schedulerEvent.get());
        assertEquals("wrong count of cluster events ", 1, clusterEvent.get());
    }

    @After
    public void shutdown() {
        // disable all listeners
        c1.setClusterListener(null);
        scheduler.setSchedulerListener(null);
        
        log("shutting down pc1...");
        pc1.stop();
        log("shutting down c1...");
        c1.stop();
        log("shutting down S...");
        scheduler.stop();

    }

}
