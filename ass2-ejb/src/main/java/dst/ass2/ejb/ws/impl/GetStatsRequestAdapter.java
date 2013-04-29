package dst.ass2.ejb.ws.impl;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import dst.ass2.ejb.ws.IGetStatsRequest;

public class GetStatsRequestAdapter extends XmlAdapter<String, IGetStatsRequest> {

    @Override
    public String marshal(IGetStatsRequest arg0) throws Exception {
        return Integer.toString(arg0.getMaxExecutions());
    }

    @Override
    public IGetStatsRequest unmarshal(String arg0) throws Exception {
        GetStatsRequest r = new GetStatsRequest();
        r.setMaxExecutions(Integer.parseInt(arg0));
        return r;
    }

}
