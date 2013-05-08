package dst.ass2.ejb.session;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.ws.Action;
import javax.xml.ws.FaultAction;
import javax.xml.ws.soap.Addressing;

import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IGrid;
import dst.ass2.ejb.dto.StatisticsDTO;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.exception.WebServiceException;
import dst.ass2.ejb.session.interfaces.IJobStatisticsBean;
import dst.ass2.ejb.ws.Constants;
import dst.ass2.ejb.ws.IGetStatsRequest;
import dst.ass2.ejb.ws.IGetStatsResponse;
import dst.ass2.ejb.ws.impl.GetStatsRequestAdapter;
import dst.ass2.ejb.ws.impl.GetStatsResponse;
import dst.ass2.ejb.ws.impl.GetStatsResponseAdapter;

@Remote(IJobStatisticsBean.class)
@Stateless
@Addressing
@WebService(serviceName = Constants.SERVICE_NAME,
            name = Constants.NAME,
            targetNamespace = Constants.NAMESPACE,
            portName = Constants.PORT_NAME)
public class JobStatisticsBean implements IJobStatisticsBean {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Action(input  = "http://localhost:8080/" + Constants.SERVICE_NAME + "/input",
            output = "http://localhost:8080/" + Constants.SERVICE_NAME + "/output",
            fault  = { @FaultAction(className = AssignmentException.class, value = "http://localhost:8080/" + Constants.SERVICE_NAME + "/input") })
    @WebMethod(operationName = "getStatisticsForGrid")
    public @XmlJavaTypeAdapter(GetStatsResponseAdapter.class) IGetStatsResponse getStatisticsForGrid (
            @XmlJavaTypeAdapter(GetStatsRequestAdapter.class) @WebParam(partName = "request") IGetStatsRequest request,
            @WebParam(header = true, partName = "name") String name) throws WebServiceException {
        if (request == null || name == null) {
            throw new WebServiceException("Invalid arguments");
        }

        final boolean gridExists = entityManager
                .createQuery("from Grid g where g.name = :gridname", IGrid.class)
                .setParameter("gridname", name)
                .getResultList()
                .size() > 0;
        if (!gridExists) {
            throw new WebServiceException("UnknownGridFault");
        }

        StatisticsDTO dto = new StatisticsDTO();
        dto.setName(name);

        List<IExecution> executions = entityManager
                .createQuery("select e from Execution e join e.computers c join c.cluster cl " +
                        "join cl.grid g where g.name = :gridname" , IExecution.class)
                .setParameter("gridname", name)
                .setMaxResults(request.getMaxExecutions())
                .getResultList();

        for (IExecution e : executions) {
            dto.addExecution(e);
        }

        return new GetStatsResponse(dto);
    }

}
