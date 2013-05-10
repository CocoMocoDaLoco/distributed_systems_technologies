package dst.ass2.ejb.ws.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import dst.ass2.ejb.dto.StatisticsDTO;
import dst.ass2.ejb.ws.IGetStatsResponse;

public class GetStatsResponseAdapter extends XmlAdapter<String, IGetStatsResponse> {

    @Override
    public String marshal(IGetStatsResponse v) throws Exception {
        JAXBContext context = JAXBContext.newInstance(StatisticsDTO.class);
        Marshaller m = context.createMarshaller();

        OutputStream os = new ByteArrayOutputStream();
        m.marshal(v.getStatistics(), os);

        return os.toString();
    }

    @Override
    public IGetStatsResponse unmarshal(String v) throws Exception {
        JAXBContext context = JAXBContext.newInstance(StatisticsDTO.class);
        Unmarshaller m = context.createUnmarshaller();

        InputStream is = new ByteArrayInputStream(v.getBytes());
        StatisticsDTO s = (StatisticsDTO)m.unmarshal(is);

        return new GetStatsResponse(s);
    }

}
