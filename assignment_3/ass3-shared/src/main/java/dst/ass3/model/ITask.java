package dst.ass3.model;

public interface ITask {

	public Long getId();

	public void setId(Long id);

	public Long getJobId();

	public void setJobId(Long jobId);

	public TaskStatus getStatus();

	public void setStatus(TaskStatus status);

	public String getRatedBy();

	public void setRatedBy(String ratedBy);

	public TaskComplexity getComplexity();

	public void setComplexity(TaskComplexity complexity);
}
