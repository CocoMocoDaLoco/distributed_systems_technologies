package dst.ass2.ejb.session.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;

import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.exception.WebServiceException;
import dst.ass2.ejb.ws.Constants;
import dst.ass2.ejb.ws.IGetStatsRequest;
import dst.ass2.ejb.ws.IGetStatsResponse;
import dst.ass2.ejb.ws.impl.GetStatsRequestAdapter;
import dst.ass2.ejb.ws.impl.GetStatsResponseAdapter;

/**
 * This is the interface of the JobStatistics Web Service.
 */
@WebService(serviceName = Constants.SERVICE_NAME,
            name = Constants.NAME,
            targetNamespace = Constants.NAMESPACE,
            portName = Constants.PORT_NAME)
public interface IJobStatisticsBean {

    /**
     * Get statistics for a given grid.
     * @param request The request object with parameters
     * @param request Name of the grid
     * @return statistics for the grid with the specified name.
     */
    @Action(input  = "http://localhost:8080/" + Constants.SERVICE_NAME + "/input",
            output = "http://localhost:8080/" + Constants.SERVICE_NAME + "/output",
            fault  = { @FaultAction(className = AssignmentException.class, value = "http://localhost:8080/" + Constants.SERVICE_NAME + "/input") })
    @WebMethod(operationName = "getStatisticsForGrid")
    @XmlJavaTypeAdapter(GetStatsResponseAdapter.class) IGetStatsResponse getStatisticsForGrid(
            @XmlJavaTypeAdapter(GetStatsRequestAdapter.class) @WebParam(partName = "request") IGetStatsRequest request,
            @WebParam(header = true, partName = "name") String gridName) throws WebServiceException;

}
