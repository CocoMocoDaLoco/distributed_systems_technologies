package dst.ass2.ejb.session;

import dst.ass2.ejb.session.exception.WebServiceException;
import dst.ass2.ejb.session.interfaces.IJobStatisticsBean;
import dst.ass2.ejb.ws.IGetStatsRequest;
import dst.ass2.ejb.ws.IGetStatsResponse;

public class JobStatisticsBean implements IJobStatisticsBean {

    public IGetStatsResponse getStatisticsForGrid(
            IGetStatsRequest request, 
            String name) throws WebServiceException {
        // TODO
        return null;
    }

}
