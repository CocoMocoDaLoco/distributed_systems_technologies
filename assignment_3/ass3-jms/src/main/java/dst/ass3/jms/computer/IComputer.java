package dst.ass3.jms.computer;

import dst.ass3.dto.ProcessTaskDTO;
import dst.ass3.model.TaskComplexity;

public interface IComputer {
	/**
	 * Starts a computer
	 */
	void start();

	/**
	 * Stops the computer and cleans all resources (e.g.: close session,
	 * connection, etc.).
	 */
	void stop();

	/**
	 * Sets the Computer-Listener. This listener simulates the execution of the
	 * given task. Only one Listener should be in use at any time. Be sure to
	 * handle cases where messages are received but no listener is yet set
	 * (discard the message).
	 * 
	 * @param listener
	 */
	void setComputerListener(IComputerListener listener);

	interface IComputerListener {
		/**
		 * Waits until the given Task has been processed. You should call this
		 * method ASYNC (in a new Thread) because it may return after a long
		 * time.
		 * 
		 * @param task
		 *            the task to simulate execution
		 * @param computerName
		 *            the name of the computer calling this listener
		 * @param acceptedComplexity
		 *            the complexity this computer accepts
		 * @param clusterName
		 *            the name of the cluster this computer belongs too
		 */
		void waitTillProcessed(ProcessTaskDTO task, String computerName,
				TaskComplexity acceptedComplexity, String clusterName);
	}
}
