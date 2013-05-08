package dst.ass2.ejb.simulator;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TimerService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.JobStatus;

@Singleton
@Startup
public class SimulatorBean {

    @Resource
    private TimerService timerService;

    @PersistenceContext
    private EntityManager entityManager;

    @Schedule(hour="*", minute = "*", second = "*/15")
    public void simulate() {
        TypedQuery<IJob> q = entityManager.createQuery("select j from Job j join j.execution e where e.end is null", IJob.class);
        List<IJob> jobs = q.getResultList();

        final Date now = new Date();

        for (IJob j : jobs) {
            if (j.getExecution().getStart().before(now)) {
                finish(j, now);
            }
        }
    }

    private void finish(IJob job, Date now) {
        IExecution e = job.getExecution();
        e.setStatus(JobStatus.FINISHED);
        e.setEnd(now);
        job.setExecutionTime((int)(now.getTime() - e.getStart().getTime()) / 1000);

        entityManager.persist(job);
    }

}
