package dst.ass2.ejb.ws;

/**
 * This interface defines the getters and setters of the 
 * GetStatsRequest Web service request object.
 */
public interface IGetStatsRequest {

	/**
	 * @return maximum number of executions in the statistics
	 */
	int getMaxExecutions();

	/**
	 * @param maxExecutions maximum number of executions in the statistics
	 */
	void setMaxExecutions(int maxExecutions);

}
