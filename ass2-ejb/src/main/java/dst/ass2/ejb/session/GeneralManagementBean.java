package dst.ass2.ejb.session;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import dst.ass1.jpa.model.IUser;
import dst.ass2.ejb.dto.AuditLogDTO;
import dst.ass2.ejb.dto.BillDTO;
import dst.ass2.ejb.management.PriceManagementBean;
import dst.ass2.ejb.management.interfaces.IPriceManagementBean;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.interfaces.IGeneralManagementBean;
import dst.ass2.ejb.util.EJBUtils;

@Remote(IGeneralManagementBean.class)
@Stateless
public class GeneralManagementBean implements IGeneralManagementBean {

    private final IPriceManagementBean priceManagementBean;

    @PersistenceContext
    private EntityManager entityManager;

    public GeneralManagementBean() throws NamingException {
        priceManagementBean = EJBUtils.lookup(new InitialContext(), PriceManagementBean.class);
    }

    @Override
    public void addPrice(Integer nrOfHistoricalJobs, BigDecimal price) {
        priceManagementBean.setPrice(nrOfHistoricalJobs, price);
    }


    @Override
    @Asynchronous
    public Future<BillDTO> getBillForUser(String username) throws Exception {
        BillDTO bill = new BillDTO();

        IUser user = null;
        try {
            user = entityManager
                    .createQuery("from User u where u.username = :username", IUser.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new AssignmentException("Could not find specified user", e);
        }

        return new AsyncResult<BillDTO>(bill);
    }

    @Override
    public List<AuditLogDTO> getAuditLogs() {
        // TODO
        return null;
    }

    @Override
    public void clearPriceCache() {
        priceManagementBean.clearCache();
    }
}
