package dst.ass1.jpa.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IJobDAO;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.impl.Job;

public class JobDAO extends GenericJpaDAO<IJob> implements IJobDAO {

    public JobDAO(Session session) {
        super(session);
    }

    @Override
    public List<IJob> findJobsForUserAndWorkflow(String user, String workflow) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IJob> findJobForStatusFinishedStartandFinish(Date start, Date finish) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTableName() {
        return Job.class.getSimpleName();
    }

}
