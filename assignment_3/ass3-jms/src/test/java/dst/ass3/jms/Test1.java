package dst.ass3.jms;

import dst.ass3.AbstractJMSTest;
import dst.ass3.dto.RateTaskDTO;
import dst.ass3.dto.TaskDTO;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import static dst.ass3.jms.cluster.ICluster.IClusterListener;
import static dst.ass3.jms.scheduler.IScheduler.ISchedulerListener;
import static dst.ass3.util.Utils.*;
import static org.junit.Assert.*;

/**
 * This test performs the following tasks: ASSIGN 1 Task and DENY it.
 * 
 * <pre>
 * Timing diagram                                                                         
 *                                                                                        
 *    0  1  2 [sec]                                                                       
 *    |--|--|-->                                                                          
 * J1 D                                                                                   
 *    ^     ^                                                                             
 *    CP1   CP2                                                                            
 *                                                                                        
 * D: Task denied by cluster                                                              
 *                                                                                        
 * J1: Job 1                                                                              
 *                                                                                        
 * CP1: Check-Point 1 - Assign job1                                                                        
 * CP2: Check-Point 2 - [Scheduler = ASSIGNED, DENNIED] [Cluster = DENNIED]
 * </pre>
 */
public class Test1 extends AbstractJMSTest {

	private AtomicInteger clusterEvent = new AtomicInteger(0);
	private AtomicInteger schedulerEvent = new AtomicInteger(0);

	private long jobId = 10;

	private long taskId = 0;

	private long startTime;

	private Semaphore sem;

	@Before
	public void init() {
		super.init();
	}

	@Test
	public void test_AssignAndDeny() {
		sem = new Semaphore(0);
		c1.start();
		c2.start();
		scheduler.start();
		pc1.start();

		IClusterListener clusterListener = new IClusterListener() {
			@Override
			public TaskDecideResponse decideTask(RateTaskDTO task,
					String clusterName) {
				logTimed("** cluster " + clusterName + " task: " + task,
						startTime);
				clusterEvent.incrementAndGet();

				assertEquals("only 1 raised event expected", 1,
						clusterEvent.get());

				assertNotNull("task.jobId = null", task.getJobId());
				assertEquals("jobId wrong", jobId, task.getJobId().longValue());

				assertNotNull("task.taskId = null", task.getId());

				log("SETTING ID " + task.getId());
				taskId = task.getId();
				sem.release();
				return new TaskDecideResponse(TaskResponse.DENY, null);
			}
		};

		ISchedulerListener schedulerListener = new ISchedulerListener() {
			@Override
			public void notify(InfoType type, TaskDTO task) {
				logTimed("** scheduler: type=" + type + " task: " + task,
						startTime);

				sleep(SHORT_WAIT); // wait short time for updated taskId

				assertEquals("jobId in server response DTO wrong "
						+ schedulerEvent, jobId, task.getJobId().longValue());
				assertEquals("taskId in server response DTO wrong"
						+ schedulerEvent, taskId, task.getId().longValue());

				switch (schedulerEvent.get()) {
				case 0:
					assertEquals("1st event of wrong type", InfoType.CREATED,
							type);
					assertEquals("1st event != ASSIGNED",
							TaskStatus.ASSIGNED, task.getStatus());
					assertEquals("first event complexity != UNRATED",
							TaskComplexity.UNRATED, task.getComplexity());
					break;
				case 1:
					assertEquals("2nd event of wrong type", InfoType.DENIED,
							type);
					assertEquals("2nd event != PROCESSING_NOT_POSSIBLE",
							TaskStatus.PROCESSING_NOT_POSSIBLE,
							task.getStatus());
					assertEquals("2nd event complexity != UNRATED",
							TaskComplexity.UNRATED, task.getComplexity());
					break;
				default:
					fail("only 2 events expected");
					break;
				}
				schedulerEvent.incrementAndGet();
				sem.release();
			}
		};

		sleep(SHORT_WAIT); // Wait for old messages coming in.
		startTime = new Date().getTime();

		// ---------------- CP1 ------------------------
		logCheckpoint(1, startTime);

		c1.setClusterListener(clusterListener);
		c2.setClusterListener(clusterListener);

		scheduler.setSchedulerListener(schedulerListener);

		log("Assigning " + jobId + "...");
		scheduler.assign(jobId);

		// ---------------- CP2 ------------------------
		logCheckpoint(2, startTime);
		assure(sem,
				3,
				"did not get 3 events (Scheduler: create, dennied; Cluster: rate) in time",
				DEFAULT_CHECK_TIMEOUT);
		assertEquals("wrong count of scheduler events ", 2,
				schedulerEvent.get());
		assertEquals("wrong count of cluster events ", 1, clusterEvent.get());
	}

	@After
	public void shutdown() {
		// disable all listeners
		c1.setClusterListener(null);
		c2.setClusterListener(null);
		scheduler.setSchedulerListener(null);

		log("shutting down pc1...");
		pc1.stop();
		log("shutting down c1...");
		c1.stop();
		log("shutting down c2...");
		c2.stop();
		log("shutting down S...");
		scheduler.stop();

	}
}
