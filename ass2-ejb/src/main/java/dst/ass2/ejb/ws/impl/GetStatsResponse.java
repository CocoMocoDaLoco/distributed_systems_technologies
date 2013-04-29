package dst.ass2.ejb.ws.impl;

import dst.ass2.ejb.dto.StatisticsDTO;
import dst.ass2.ejb.ws.IGetStatsResponse;

public class GetStatsResponse implements IGetStatsResponse {

    private final StatisticsDTO statistics;

    public GetStatsResponse(StatisticsDTO statistics) {
        this.statistics = statistics;
    }

    @Override
    public StatisticsDTO getStatistics() {
        return statistics;
    }

}
