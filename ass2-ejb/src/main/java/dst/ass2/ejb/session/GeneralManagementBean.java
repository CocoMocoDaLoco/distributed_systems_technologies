package dst.ass2.ejb.session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
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

import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IUser;
import dst.ass2.ejb.dto.AuditLogDTO;
import dst.ass2.ejb.dto.BillDTO;
import dst.ass2.ejb.dto.BillDTO.BillPerJob;
import dst.ass2.ejb.management.PriceManagementBean;
import dst.ass2.ejb.management.interfaces.IPriceManagementBean;
import dst.ass2.ejb.model.IAuditLog;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.interfaces.IGeneralManagementBean;
import dst.ass2.ejb.util.EJBUtils;

/* "Share not state, invoked independently of each other, invokable by client directly. */

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
        bill.setBills(new LinkedList<BillDTO.BillPerJob>());
        bill.setUsername(username);
        bill.setTotalPrice(new BigDecimal(0));

        IUser user = null;
        try {
            user = entityManager
                    .createQuery("from User u where u.username = :username", IUser.class)
                    .setParameter("username", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new AssignmentException("Could not find specified user", e);
        }

        List<IJob> unpaidJobs = entityManager
                .createQuery("from Job j where j.user.username = :username and isPaid = false", IJob.class)
                .setParameter("username", username)
                .getResultList();

        final int totalJobCount = entityManager
                .createQuery("from Job j where j.user.username = :username", IJob.class)
                .setParameter("username", username)
                .getResultList().size();

        int paidJobs = totalJobCount - unpaidJobs.size();

        for (IJob job : unpaidJobs) {
            billJob(paidJobs++, bill, job, user);
        }

        entityManager.flush();

        return new AsyncResult<BillDTO>(bill);
    }

    private void billJob(int paidJobs, BillDTO bill, IJob job, IUser user) throws AssignmentException {
        final List<IComputer> computers = job.getExecution().getComputers();
        if (computers.isEmpty()) {
            System.out.printf("Job not assigned to any computers%n");
            throw new AssignmentException("Job not assigned to any computers");
        }

        final IGrid grid = computers.get(0).getCluster().getGrid();

        List<IMembership> memberships = entityManager
                .createQuery("select m from User u join u.memberships m join m.grid g " +
                        "where u.username = :username and g.id = :gridid", IMembership.class)
                        .setParameter("username", user.getUsername())
                        .setParameter("gridid", grid.getId())
                        .getResultList();

        final BigDecimal discount = (memberships.size() > 0) ? new BigDecimal(memberships.get(0).getDiscount()) : new BigDecimal(0);
        final BigDecimal fraction = new BigDecimal(1).subtract(discount);
        final BigDecimal cs = priceManagementBean.getPrice(paidJobs).multiply(fraction);
        final BigDecimal executionMinutes = new BigDecimal(job.getExecutionTime() / 60);
        final BigDecimal ce = grid.getCostsPerCPUMinute().multiply(executionMinutes).multiply(fraction);

        BillPerJob bpj = bill.new BillPerJob();
        bpj.setJobId(job.getId());
        bpj.setNumberOfComputers(job.getExecution().getComputers().size());
        bpj.setSetupCosts(cs.setScale(2, RoundingMode.HALF_UP));
        bpj.setExecutionCosts(ce.setScale(2, RoundingMode.HALF_UP));
        bpj.setJobCosts(ce.add(cs).setScale(2, RoundingMode.HALF_UP));

        bill.getBills().add(bpj);
        bill.setTotalPrice(bill.getTotalPrice().add(bpj.getJobCosts()));

        job.setPaid(true);
        entityManager.persist(job);
    }

    @Override
    public List<AuditLogDTO> getAuditLogs() {
        List<AuditLogDTO> out = new LinkedList<AuditLogDTO>();

        List<IAuditLog> logs = entityManager.createQuery("from AuditLog", IAuditLog.class).getResultList();
        for (IAuditLog log : logs) {
            out.add(new AuditLogDTO(log));
        }

        return out;
    }

    @Override
    public void clearPriceCache() {
        priceManagementBean.clearCache();
    }
}
