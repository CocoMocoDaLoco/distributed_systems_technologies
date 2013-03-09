package dst.ass1.jpa.model;

public interface IJob {

	public Long getId();

	public void setId(Long id);

	public Integer getNumCPUs();

	public void setNumCPUs(Integer numCPUs);

	public Integer getExecutionTime();

	public void setExecutionTime(Integer executionTime);

	public boolean isPaid();

	public void setPaid(boolean isPaid);

	public IEnvironment getEnvironment();

	public void setEnvironment(IEnvironment environment);

	public IUser getUser();

	public void setUser(IUser user);

	public IExecution getExecution();

	public void setExecution(IExecution execution);
}
