package dst.ass2.ejb.session;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dst.ass1.jpa.model.IExecution;
import dst.ass2.ejb.dto.StatisticsDTO;
import dst.ass2.ejb.session.exception.WebServiceException;
import dst.ass2.ejb.session.interfaces.IJobStatisticsBean;
import dst.ass2.ejb.ws.IGetStatsRequest;
import dst.ass2.ejb.ws.IGetStatsResponse;
import dst.ass2.ejb.ws.impl.GetStatsResponse;

@Remote(IJobStatisticsBean.class)
@Stateless
public class JobStatisticsBean implements IJobStatisticsBean {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public IGetStatsResponse getStatisticsForGrid(
            IGetStatsRequest request,
            String name) throws WebServiceException {
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
