package dst.ass2.ejb.dto;

import dst.ass2.ejb.model.IAuditLog;
import dst.ass2.ejb.model.IAuditParameter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AuditLogDTO implements Serializable {

	private static final long serialVersionUID = 4824305617969183140L;
	private Long id;
	private String method;
	private String result;
	private Date invocationTime;
	private List<AuditParameterDTO> parameters;

	public AuditLogDTO(Long id, String method, String result,
			Date invocationTime, List<AuditParameterDTO> parameters) {
		super();
		this.id = id;
		this.method = method;
		this.result = result;
		this.invocationTime = invocationTime;
		this.parameters = parameters;
	}
	
	public AuditLogDTO(IAuditLog auditLog) {
		super();
		this.id = auditLog.getId();
		this.method = auditLog.getMethod();
		this.result = auditLog.getResult();
		this.invocationTime = auditLog.getInvocationTime();
		this.parameters = new ArrayList<AuditParameterDTO>();
		
		for(IAuditParameter parameter : auditLog.getParameters())
			this.parameters.add(new AuditParameterDTO(parameter));
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public Date getInvocationTime() {
		return invocationTime;
	}

	public void setInvocationTime(Date invocationTime) {
		this.invocationTime = invocationTime;
	}

	public List<AuditParameterDTO> getParameters() {
		return parameters;
	}

	public void setParameters(List<AuditParameterDTO> parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "AuditLogDTO [id=" + id + ", method=" + method + ", parameters="
				+ parameters + ", result=" + result + ", invocationTime="
				+ invocationTime + "]";
	}

}
