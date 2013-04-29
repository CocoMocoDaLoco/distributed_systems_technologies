package dst.ass2.ejb.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import dst.ass2.ejb.dto.AssignmentDTO;
import dst.ass2.ejb.session.exception.AssignmentException;
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
        List<Long> ids = new LinkedList<Long>();

        AssignmentDTO dto = new AssignmentDTO(gridId, numCPUs, workflow, params, ids);
        cache.add(dto);

        /* TODO */
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
