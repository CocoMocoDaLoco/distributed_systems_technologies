package dst.ass1.jpa.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IJobDAO;
import dst.ass1.jpa.model.IJob;

public class JobDAO implements IJobDAO {

    public JobDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IJob findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IJob> findAll() {
        // TODO Auto-generated method stub
        return null;
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

}
