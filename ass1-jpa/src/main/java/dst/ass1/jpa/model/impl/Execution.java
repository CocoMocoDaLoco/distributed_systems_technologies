package dst.ass1.jpa.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.JobStatus;

@Entity
public class Execution implements IExecution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date start;

    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    @Enumerated(value = EnumType.STRING)
    private JobStatus status;

    @ManyToMany(targetEntity = Computer.class, mappedBy = "executions", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<IComputer> computers = new ArrayList<IComputer>();

    /* TODO: Why does inserting optional = false break multiple test cases? */
    @OneToOne(targetEntity = Job.class)
    private IJob job;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Date getStart() {
        return start;
    }

    @Override
    public void setStart(Date start) {
        this.start = start;
    }

    @Override
    public Date getEnd() {
        return end;
    }

    @Override
    public void setEnd(Date end) {
        this.end = end;
    }

    @Override
    public JobStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public List<IComputer> getComputers() {
        return computers;
    }

    @Override
    public void setComputers(List<IComputer> computers) {
        this.computers = computers;
    }

    @Override
    public void addComputer(IComputer computer) {
        computers.add(computer);
    }

    @Override
    public IJob getJob() {
        return job;
    }

    @Override
    public void setJob(IJob job) {
        this.job = job;
    }

}
