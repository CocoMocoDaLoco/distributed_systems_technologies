package dst.ass2.ejb.ws.impl;

import dst.ass2.ejb.ws.IGetStatsRequest;

public class GetStatsRequest implements IGetStatsRequest {

    private int maxExecutions;

    @Override
    public int getMaxExecutions() {
        return maxExecutions;
    }

    @Override
    public void setMaxExecutions(int maxExecutions) {
        this.maxExecutions = maxExecutions;
    }

}
