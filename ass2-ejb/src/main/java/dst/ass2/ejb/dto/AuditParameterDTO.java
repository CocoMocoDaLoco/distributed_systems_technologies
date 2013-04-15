package dst.ass2.ejb.dto;

import java.io.Serializable;

import dst.ass2.ejb.model.IAuditParameter;

public class AuditParameterDTO implements Serializable {

	private static final long serialVersionUID = -4530699588044654702L;
	private Long id;
	private Integer parameterIndex;
	private String type;
	private String value;

	public AuditParameterDTO(Long id, Integer parameterIndex, String type,
			String value) {
		super();
		this.id = id;
		this.parameterIndex = parameterIndex;
		this.type = type;
		this.value = value;
	}

	public AuditParameterDTO(IAuditParameter parameter) {
		super();
		this.id = parameter.getId();
		this.parameterIndex = parameter.getParameterIndex();
		this.type = parameter.getType();
		this.value = parameter.getValue();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getParameterIndex() {
		return parameterIndex;
	}

	public void setParameterIndex(Integer parameterIndex) {
		this.parameterIndex = parameterIndex;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "AuditParameterDTO [id=" + id + ", parameterIndex="
				+ parameterIndex + ", type=" + type + ", value=" + value + "]";
	}

}
