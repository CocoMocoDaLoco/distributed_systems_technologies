package dst.ass2.ejb.model.impl;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import dst.ass2.ejb.model.IAuditLog;
import dst.ass2.ejb.model.IAuditParameter;

@Entity
public class AuditParameter implements IAuditParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer parameterIndex;
    private String type;
    private String value;
    private IAuditLog auditLog;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getParameterIndex() {
        return parameterIndex;
    }

    @Override
    public void setParameterIndex(Integer parameterIndex) {
        this.parameterIndex = parameterIndex;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public IAuditLog getAuditLog() {
        return auditLog;
    }

    @Override
    public void setAuditLog(IAuditLog auditLog) {
        this.auditLog = auditLog;
    }

}
