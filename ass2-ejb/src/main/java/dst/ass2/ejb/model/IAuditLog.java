package dst.ass2.ejb.model;

import java.util.Date;
import java.util.List;

public interface IAuditLog {

	public Long getId();

	public void setId(Long id);

	public String getMethod();

	public void setMethod(String method);

	public String getResult();

	public void setResult(String result);

	public Date getInvocationTime();

	public void setInvocationTime(Date invocationTime);

	public List<IAuditParameter> getParameters();

	public void setParameters(List<IAuditParameter> parameters);

}