package dst.ass2.ejb.session;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Singleton;

import dst.ass2.ejb.dto.AssignmentDTO;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.interfaces.IJobManagementBean;

/* TODO: Other bean type? */

@Remote(IJobManagementBean.class)
@Singleton
public class JobManagementBean implements IJobManagementBean {

    // TODO


    @Override
    public void addJob(Long gridId, Integer numCPUs, String workflow,
            List<String> params) throws AssignmentException {
        // TODO
    }


    @Override
    public void login(String username, String password)
            throws AssignmentException {
        // TODO
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
