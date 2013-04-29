package dst.ass2.ejb.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.JobStatus;
import dst.ass1.jpa.model.impl.Environment;
import dst.ass1.jpa.model.impl.Execution;
import dst.ass1.jpa.model.impl.Job;
import dst.ass2.ejb.dto.AssignmentDTO;
import dst.ass2.ejb.interceptor.AuditInterceptor;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.exception.CapacityExceededException;
import dst.ass2.ejb.session.interfaces.IJobManagementBean;

/* Stateful, because state is unique to a client/bean session. */

@Remote(IJobManagementBean.class)
@Stateful
@Interceptors({AuditInterceptor.class})
public class JobManagementBean implements IJobManagementBean {

    @PersistenceContext
    private EntityManager entityManager;

    private boolean isLoggedIn = false;
    private String username = null;

    List<AssignmentDTO> cache = new LinkedList<AssignmentDTO>();

    @Override
    public void addJob(Long gridId, Integer numCPUs, String workflow,
            List<String> params) throws AssignmentException {
        TypedQuery<IComputer> q = entityManager.createQuery("from Computer", IComputer.class);
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
            boolean inGrid = c.getCluster().getGrid().getId().equals(gridId);

            boolean isFree = true;
            for (IExecution e : c.getExecutions()) {
                if (e.getStatus() != JobStatus.FAILED && e.getStatus() != JobStatus.FINISHED) {
                    isFree = false;
                    break;
                }
            }

            if (inGrid && isFree) {
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
        this.username = null;

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new AssignmentException(e);
        }
        byte[] b = md.digest(password.getBytes());

        TypedQuery<IUser> q = entityManager.createQuery("from User " +
                "where username = :username and password = :password", IUser.class);
        q.setParameter("username", username);
        q.setParameter("password", b);

        int count = q.getResultList().size();

        if (count != 1) {
            throw new AssignmentException("Incorrect user or password");
        }

        isLoggedIn = true;
        this.username = q.getSingleResult().getUsername();
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
    @Remove(retainIfException = true)
    public void submitAssignments() throws AssignmentException {
        if (!isLoggedIn) {
            throw new AssignmentException("Not logged in");
        }

        TypedQuery<IComputer> q = entityManager.createQuery("from Computer", IComputer.class);
        List<IComputer> computers = q.getResultList();

        Map<Long, IComputer> computerMap = new HashMap<Long, IComputer>();
        for (IComputer c : computers) {
            computerMap.put(c.getId(), c);
        }

        if (!isValidAssignment(cache, computers)) {
            throw new AssignmentException("Assignment is invalid");
        }

        for (AssignmentDTO dto : cache) {
            persist(computerMap, dto);
        }

        entityManager.flush();
    }

    private void persist(Map<Long, IComputer> computerMap, AssignmentDTO dto) {
        final Date now = new Date();

        IUser user = entityManager.createQuery("from User where username = :username", IUser.class)
                .setParameter("username", username)
                .getSingleResult();

        Environment environment = new Environment();
        Job job = new Job();
        Execution execution = new Execution();
        List<IComputer> computers = new LinkedList<IComputer>();

        for (long id : dto.getComputerIds()) {
            computers.add(computerMap.get(id));
        }

        environment.setParams(dto.getParams());
        environment.setWorkflow(dto.getWorkflow());

        job.setEnvironment(environment);
        job.setExecution(execution);
        job.setUser(user);
        job.setNumCPUs(dto.getNumCPUs());
        job.setPaid(false);

        execution.setComputers(computers);
        execution.setStart(now);
        execution.setJob(job);
        execution.setStatus(JobStatus.SCHEDULED);

        entityManager.persist(job);
    }

    private boolean isValidAssignment(List<AssignmentDTO> assignment, List<IComputer> computers) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public List<AssignmentDTO> getCache() {
        return cache;
    }
}
