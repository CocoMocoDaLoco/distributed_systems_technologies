package dst.ass1.jpa.model;

import java.util.Date;
import java.util.List;

public interface IExecution {

	public Long getId();

	public void setId(Long id);

	public Date getStart();

	public void setStart(Date start);

	public Date getEnd();

	public void setEnd(Date end);

	public JobStatus getStatus();

	public void setStatus(JobStatus status);

	public List<IComputer> getComputers();

	public void setComputers(List<IComputer> computers);

	public void addComputer(IComputer computer);

	public IJob getJob();

	public void setJob(IJob job);

}
