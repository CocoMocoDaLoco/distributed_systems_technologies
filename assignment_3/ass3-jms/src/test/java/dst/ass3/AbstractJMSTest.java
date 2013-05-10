package dst.ass3;

import dst.ass3.jms.JMSFactory;
import dst.ass3.jms.cluster.ICluster;
import dst.ass3.jms.computer.IComputer;
import dst.ass3.jms.scheduler.IScheduler;
import dst.ass3.model.TaskComplexity;

public abstract class AbstractJMSTest {
	protected IScheduler scheduler;
	protected ICluster c2;
	protected ICluster c1;
	protected IComputer pc4;
	protected IComputer pc3;
	protected IComputer pc2;
	protected IComputer pc1;

	/**
	 * Time to wait for semaphores to reach required value
	 */
	public int DEFAULT_CHECK_TIMEOUT = 5;
	
	public void init() {
		this.pc1 = JMSFactory.createComputer("pc1", "c1", TaskComplexity.CHALLENGING);
		this.pc2 = JMSFactory.createComputer("pc2", "c1", TaskComplexity.EASY);
		this.pc3 = JMSFactory.createComputer("pc3", "c2", TaskComplexity.CHALLENGING);
		this.pc4 = JMSFactory.createComputer("pc4", "c2", TaskComplexity.EASY);

		this.c1 = JMSFactory.createCluster("c1");
		this.c2 = JMSFactory.createCluster("c2");

		this.scheduler = JMSFactory.createScheduler();

		System.out.println("******************************" + this.getClass().getCanonicalName()
				+ "******************************");
	}


}
