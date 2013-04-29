package dst.ass2.ejb.session;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.ejb.Remote;
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

    @Override
    public void addJob(Long gridId, Integer numCPUs, String workflow,
            List<String> params) throws AssignmentException {
        // TODO
    }


    @Override
    public void login(String username, String password)
            throws AssignmentException {
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
    }

    @Override
    public void removeJobsForGrid(Long gridId) {
        // TODO
    }

    @Override
    public void submitAssignments() throws AssignmentException {
        // TODO
    }

    @Override
    public List<AssignmentDTO> getCache() {
        // TODO
        return null;
    }

}
