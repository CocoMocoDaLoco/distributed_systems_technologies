package dst.ass3.event;

import com.espertech.esper.client.StatementAwareUpdateListener;

import dst.ass3.model.ITask;

/**
 * Connection between the test cases and the Esper 
 * eventing logic implemented by students.
 */
public interface IEventProcessing {
	/**
	 * This method initializes the Esper engine with all required imports, 
	 * data types, schemas, and queries. Use the event names provided in 
	 * the class {@link Constants}.
	 * Note: This method should not add any test data to Esper.
	 *
	 * @param listener The update listener to register with Esper. 
	 * The listener is used by the tests to check whether the queries are correct 
	 * and events are correctly processed. 
	 * Make sure to report all new events to this listener (in particular, new 
	 * events of type TaskAssigned, TaskProcessed, TaskDuration, and AvgTaskDuration). 
	 * Important: Make sure that the update listener receives all updates 
	 * exactly once (i.e., no duplicate update notifications).
	 *
	 * @param debug If true, then set the following values in the Esper config:
	 * config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
	 * config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
	 * config.getEngineDefaults().getLogging().setEnableQueryPlan(false);
	 */
	void initializeAll(StatementAwareUpdateListener listener, boolean debug);
	
	/**
	 * Send an event to the Esper runtime.
	 * @param task the new Task event
	 */
	void addEvent(ITask task);
	
	/**
	 * Close all listeners and free all resources.
	 */
	void close();
	
}
