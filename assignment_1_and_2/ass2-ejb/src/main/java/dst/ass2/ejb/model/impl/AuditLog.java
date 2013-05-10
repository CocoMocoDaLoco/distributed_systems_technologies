package dst.ass2.ejb.model.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import dst.ass2.ejb.model.IAuditLog;
import dst.ass2.ejb.model.IAuditParameter;

@Entity
public class AuditLog implements IAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String method;
    private Date invocationTime;

    @OneToMany(targetEntity = AuditParameter.class, cascade = CascadeType.ALL, mappedBy = "auditLog")
    private List<IAuditParameter> parameters;
    private String result;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getResult() {
        return result;
    }

    @Override
    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public Date getInvocationTime() {
        return invocationTime;
    }

    @Override
    public void setInvocationTime(Date invocationTime) {
        this.invocationTime = invocationTime;
    }

    @Override
    public List<IAuditParameter> getParameters() {
        return parameters;
    }

    @Override
    public void setParameters(List<IAuditParameter> parameters) {
        this.parameters = parameters;
    }

}
