package dst.ass1.jpa.model.impl;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

import dst.ass1.jpa.model.IEnvironment;

@Entity
public class Environment implements IEnvironment {

    @Id
    private Long id;
    private String workflow;

    @ElementCollection
    private List<String> params;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getWorkflow() {
        return workflow;
    }

    @Override
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    @Override
    public List<String> getParams() {
        return params;
    }

    @Override
    public void setParams(List<String> params) {
        this.params = params;
    }

    @Override
    public void addParam(String param) {
        params.add(param);
    }

}
