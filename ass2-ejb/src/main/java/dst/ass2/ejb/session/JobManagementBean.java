package dst.ass2.ejb.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import dst.ass1.jpa.model.IComputer;
import dst.ass2.ejb.dto.AssignmentDTO;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.exception.CapacityExceededException;
import dst.ass2.ejb.session.interfaces.IJobManagementBean;

/* Stateful, because state is unique to a client/bean session. */

@Remote(IJobManagementBean.class)
@Stateful
public class JobManagementBean implements IJobManagementBean {

    @PersistenceContext
    private EntityManager entityManager;

    private boolean isLoggedIn = false;

    List<AssignmentDTO> cache = new LinkedList<AssignmentDTO>();

    @Override
    public void addJob(Long gridId, Integer numCPUs, String workflow,
            List<String> params) throws AssignmentException {
        Query q = entityManager.createQuery("from Computer");
        @SuppressWarnings("unchecked")
        List<IComputer> computers = q.getResultList();

        if (exceedsCapacity(computers, cache, gridId, numCPUs)) {
            throw new CapacityExceededException();
        }

        List<Long> ids = schedule(computers, cache, gridId, numCPUs);

        AssignmentDTO dto = new AssignmentDTO(gridId, numCPUs, workflow, params, ids);
        cache.add(dto);
    }

    private boolean exceedsCapacity(List<IComputer> computers, List<AssignmentDTO> assignments,
            Long gridId, Integer numCPUs) {
        Collection<IComputer> computersInGrid =
                availableComputersInGrid(computers, assignments, gridId);

        int availableCPUs = 0;
        for (IComputer c : computersInGrid) {
            availableCPUs += c.getCpus();
        }

        return (availableCPUs < numCPUs);
    }

    private Collection<IComputer> availableComputersInGrid(List<IComputer> computers,
            List<AssignmentDTO> assignments, Long gridId) {
        Map<Long, IComputer> computersInGrid = new HashMap<Long, IComputer>();
        for (IComputer c : computers) {
            if (c.getCluster().getGrid().getId().equals(gridId)) {
                computersInGrid.put(c.getId(), c);
            }
        }

        for (AssignmentDTO a : assignments) {
            for (Long id : a.getComputerIds()) {
                computersInGrid.remove(id);
            }
        }

        return computersInGrid.values();
    }

    private List<Long> schedule(List<IComputer> computers, List<AssignmentDTO> assignments,
            Long gridId, Integer numCPUs) {
        Collection<IComputer> computersInGrid =
                availableComputersInGrid(computers, assignments, gridId);

        int remainingCPUs = numCPUs;
        List<Long> ids = new LinkedList<Long>();

        for (IComputer c : computersInGrid) {
            ids.add(c.getId());
            remainingCPUs -= c.getCpus();

            if (remainingCPUs <= 0) {
                break;
            }
        }

        return ids;
    }

    @Override
    public void login(String username, String password)
            throws AssignmentException {
        isLoggedIn = false;

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new AssignmentException(e);
        }
        byte[] b = md.digest(password.getBytes());

        Query q = entityManager.createQuery("from User " +
                "where username = :username and password = :password");
        q.setParameter("username", username);
        q.setParameter("password", b);

        int count = q.getResultList().size();

        if (count != 1) {
            throw new AssignmentException("Incorrect user or password");
        }

        isLoggedIn = true;
    }

    @Override
    public void removeJobsForGrid(Long gridId) {
        for (int i = cache.size() - 1; i >= 0; i--) {
            if (cache.get(i).getGridId().equals(gridId)) {
                cache.remove(i);
            }
        }
    }

    @Override
    public void submitAssignments() throws AssignmentException {
        if (!isLoggedIn) {
            throw new AssignmentException("Not logged in");
        }

        /* TODO */

        remove();
    }

    @Override
    public List<AssignmentDTO> getCache() {
        return cache;
    }

    @Remove
    private void remove() { }
}
