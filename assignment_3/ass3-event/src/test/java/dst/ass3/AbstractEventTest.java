package dst.ass3;

import org.junit.After;
import org.junit.Before;

import com.espertech.esper.client.EventBean;

import dst.ass3.event.EventingFactory;
import dst.ass3.event.IEventProcessing;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

public abstract class AbstractEventTest {

	protected final int allowedInaccuracy = 500;

	protected IEventProcessing test;

	@Before
	public void setup() {
		System.out.println("******************************"
				+ this.getClass().getCanonicalName()
				+ "******************************");

		test = EventingFactory.getInstance();
	}

	@After
	public void shutdown() {
		test.close();
	}

	protected TaskComplexity getTaskComplexity(EventBean e) {
		try {
			return (TaskComplexity) e.get("complexity");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}

	protected TaskStatus getTaskStatus(EventBean e) {
		try {
			return (TaskStatus) e.get("status");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
