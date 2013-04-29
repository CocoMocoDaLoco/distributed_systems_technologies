package dst.ass2.ejb.ws;

import dst.ass2.ejb.ws.impl.GetStatsRequest;

/**
 * Factory used to instantiate Web service request objects.
 */
public class WSRequestFactory {

    /**
     * Create an instance of IGetStatsRequest
     * @return
     */
    public IGetStatsRequest createGetStatsRequest() {
        return new GetStatsRequest();
    }

}
