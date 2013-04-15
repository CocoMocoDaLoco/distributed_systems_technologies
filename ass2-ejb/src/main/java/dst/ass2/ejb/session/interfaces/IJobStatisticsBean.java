package dst.ass2.ejb.session.interfaces;

import dst.ass2.ejb.session.exception.WebServiceException;
import dst.ass2.ejb.ws.IGetStatsRequest;
import dst.ass2.ejb.ws.IGetStatsResponse;

/**
 * This is the interface of the JobStatistics Web Service.
 */
public interface IJobStatisticsBean {

	/**
	 * Get statistics for a given grid.
	 * @param request The request object with parameters
	 * @param request Name of the grid
	 * @return statistics for the grid with the specified name.
	 */
	IGetStatsResponse getStatisticsForGrid(
			IGetStatsRequest request, 
			String gridName) throws WebServiceException;

}
