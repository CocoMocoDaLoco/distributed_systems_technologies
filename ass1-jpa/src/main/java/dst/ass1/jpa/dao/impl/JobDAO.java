package dst.ass1.jpa.dao.impl;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import dst.ass1.jpa.dao.IJobDAO;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.JobStatus;
import dst.ass1.jpa.model.impl.Execution;
import dst.ass1.jpa.model.impl.Job;

public class JobDAO extends GenericJpaDAO<IJob> implements IJobDAO {

    public JobDAO(Session session) {
        super(session);
    }

    @Override
    public List<IJob> findJobsForUserAndWorkflow(String user, String workflow) {
        Criteria c = getSession().createCriteria(Job.class)
                .createAlias("user", "u")
                .createAlias("environment", "e");

        if (user != null && !user.isEmpty()) {
            c.add(Restrictions.eq("u.username", user));
        }

        if (workflow != null && !workflow.isEmpty()) {
            c.add(Restrictions.eq("e.workflow", workflow));
        }

        @SuppressWarnings("unchecked")
        List<IJob> results = c.list();

        return results;
    }

    @Override
    public List<IJob> findJobForStatusFinishedStartandFinish(Date start, Date finish) {
        Execution execution = new Execution();
        execution.setStatus(JobStatus.FINISHED);
        execution.setStart(start);
        execution.setEnd(finish);

        Job job = new Job();
        job.setExecution(execution);

        Criteria c = getSession().createCriteria(Job.class)
                .add(Example.create(job))
                .createCriteria("execution")
                .add(Example.create(job.getExecution()));

        @SuppressWarnings("unchecked")
        List<IJob> results = c.list();

        return results;
    }

    @Override
    protected String getTableName() {
        return Job.class.getSimpleName();
    }

}
