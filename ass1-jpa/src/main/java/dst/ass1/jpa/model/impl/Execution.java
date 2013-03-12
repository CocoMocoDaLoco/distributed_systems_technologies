package dst.ass1.jpa.model.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.hibernate.annotations.GenericGenerator;

import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.JobStatus;

@Entity
public class Execution implements IExecution {

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    private Long id;
    private Date start;
    private Date end;
    private JobStatus status;

    @ElementCollection(targetClass = Computer.class)
    private List<IComputer> computers;

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