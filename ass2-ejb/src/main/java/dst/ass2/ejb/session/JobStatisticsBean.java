package dst.ass2.ejb.session;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import dst.ass2.ejb.session.exception.WebServiceException;
import dst.ass2.ejb.session.interfaces.IJobStatisticsBean;
import dst.ass2.ejb.ws.IGetStatsRequest;
import dst.ass2.ejb.ws.IGetStatsResponse;

/* TODO: Other bean type? */

@Remote(IJobStatisticsBean.class)
@Singleton
public class JobStatisticsBean implements IJobStatisticsBean {

    @Override
    public IGetStatsResponse getStatisticsForGrid(
            IGetStatsRequest request,
            String name) throws WebServiceException {
        // TODO
        return null;
    }

}
