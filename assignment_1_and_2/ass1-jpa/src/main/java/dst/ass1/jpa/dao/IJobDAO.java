package dst.ass1.jpa.dao;

import dst.ass1.jpa.model.IJob;

import java.util.Date;
import java.util.List;

public interface IJobDAO extends GenericDAO<IJob> {
	List<IJob> findJobsForUserAndWorkflow(String user, String workflow);
	List<IJob> findJobForStatusFinishedStartandFinish(Date start,Date finish);
}
