package dst.ass2.ejb.ws;

import dst.ass2.ejb.dto.StatisticsDTO;

/**
 * This interface defines the getters and setters of the 
 * GetStatsResponse Web service response object.
 */
public interface IGetStatsResponse {

	StatisticsDTO getStatistics();

}
