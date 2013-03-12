package dst.ass1.jpa.model.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.GenericGenerator;

import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IExecution;

@Entity
public class Computer implements IComputer {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private String name;
    private Integer cpus;
    private String location;
    private Date creation;
    private Date lastUpdate;

    @ManyToOne(targetEntity = Cluster.class)
    private ICluster cluster;

    @ElementCollection(targetClass = Execution.class)
    private List<IExecution> executions;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Integer getCpus() {
        return cpus;
    }

    @Override
    public void setCpus(Integer cpus) {
        this.cpus = cpus;
    }

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public Date getCreation() {
        return creation;
    }

    @Override
    public void setCreation(Date creation) {
        this.creation = creation;
    }

    @Override
    public Date getLastUpdate() {
        return lastUpdate;
    }

    @Override
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public ICluster getCluster() {
        return cluster;
    }

    @Override
    public void setCluster(ICluster cluster) {
        this.cluster = cluster;
    }

    @Override
    public List<IExecution> getExecutions() {
        return executions;
    }

    @Override
    public void setExecutions(List<IExecution> executions) {
        this.executions = executions;
    }

    @Override
    public void addExecution(IExecution execution) {
        executions.add(execution);
    }

}
