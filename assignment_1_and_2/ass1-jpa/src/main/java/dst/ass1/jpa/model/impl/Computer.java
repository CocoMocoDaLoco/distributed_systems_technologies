package dst.ass1.jpa.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import dst.ass1.jpa.listener.ComputerListener;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.validator.CPUs;

@EntityListeners({ComputerListener.class})
@Entity
public class Computer implements IComputer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 5, max = 25)
    @Column(unique = true)
    private String name;

    @CPUs(min = 4, max = 8)
    private Integer cpus;

    @Pattern(regexp = "[A-Z]{3}-[A-Z]{3}@[0-9]{4,}")
    private String location;

    @Past
    private Date creation;

    @Past
    private Date lastUpdate;

    @ManyToOne(targetEntity = Cluster.class, optional = false, cascade = { CascadeType.PERSIST,CascadeType.MERGE })
    private ICluster cluster;

    @ManyToMany(targetEntity = Execution.class, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "execution_computer",
               joinColumns = @JoinColumn(name = "computers_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "executions_id", referencedColumnName = "id"))
    private List<IExecution> executions = new ArrayList<IExecution>();

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
