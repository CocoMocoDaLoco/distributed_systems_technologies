package dst.ass2.ejb.model;

public interface IAuditParameter {

	public Long getId();

	public void setId(Long id);

	public Integer getParameterIndex();

	public void setParameterIndex(Integer parameterIndex);

	public String getType();

	public void setType(String type);

	public String getValue();

	public void setValue(String value);

	public IAuditLog getAuditLog();

	public void setAuditLog(IAuditLog auditLog);

}