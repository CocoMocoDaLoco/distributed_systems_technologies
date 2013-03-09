package dst.ass1.jpa.model;

import java.util.List;

public interface IEnvironment {
	
	public Long getId();

	public void setId(Long id);

	public String getWorkflow();

	public void setWorkflow(String workflow);

	public List<String> getParams();

	public void setParams(List<String> params);

	public void addParam(String param);

}
