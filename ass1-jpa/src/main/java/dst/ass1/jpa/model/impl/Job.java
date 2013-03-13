package dst.ass1.jpa.model.impl;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import dst.ass1.jpa.model.IEnvironment;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IUser;

@Entity
public class Job implements IJob {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer numCPUs;
    private Integer executionTime;
    private boolean isPaid;

    @OneToOne(targetEntity = Environment.class, cascade = CascadeType.PERSIST)
    private IEnvironment environment;

    @ManyToOne(targetEntity = User.class)
    private IUser user;

    @OneToOne(targetEntity = Execution.class, cascade = CascadeType.PERSIST)
    private IExecution execution;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Integer getNumCPUs() {
        return numCPUs;
    }

    @Override
    public void setNumCPUs(Integer numCPUs) {
        this.numCPUs = numCPUs;
    }

    @Override
    public Integer getExecutionTime() {
        return executionTime;
    }

    @Override
    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }

    @Override
    public boolean isPaid() {
        return isPaid;
    }

    @Override
    public void setPaid(boolean isPaid) {
        this.isPaid = isPaid;
    }

    @Override
    public IEnvironment getEnvironment() {
        return environment;
    }

    @Override
    public void setEnvironment(IEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public IExecution getExecution() {
        return execution;
    }

    @Override
    public void setExecution(IExecution execution) {
        this.execution = execution;
    }

}
