package dst.ass1.jpa.model;

import java.util.Date;
import java.util.List;

public interface IComputer {
	public Long getId();

	public void setId(Long id);

	public String getName();

	public void setName(String name);

	public Integer getCpus();

	public void setCpus(Integer cpus);

	public String getLocation();

	public void setLocation(String location);

	public Date getCreation();

	public void setCreation(Date creation);

	public Date getLastUpdate();

	public void setLastUpdate(Date lastUpdate);

	public ICluster getCluster();

	public void setCluster(ICluster cluster);

	public List<IExecution> getExecutions();

	public void setExecutions(List<IExecution> executions);

	public void addExecution(IExecution execution);
}
