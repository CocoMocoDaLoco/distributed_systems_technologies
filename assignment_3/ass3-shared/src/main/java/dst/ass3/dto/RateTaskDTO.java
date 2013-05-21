package dst.ass3.dto;

import java.io.Serializable;

import dst.ass3.model.ITask;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

public class RateTaskDTO implements Serializable {

    private static final long serialVersionUID = 3245360514579863868L;
    private Long id;
    private Long jobId;
    private TaskStatus status;
    private String ratedBy;
    private TaskComplexity complexity;

    public RateTaskDTO() {
    }

    public RateTaskDTO(Long id, Long jobId, TaskStatus status, String ratedBy,
            TaskComplexity complexity) {
        super();
        this.id = id;
        this.jobId = jobId;
        this.status = status;
        this.ratedBy = ratedBy;
        this.complexity = complexity;
    }

    public RateTaskDTO(ITask task) {
        super();
        this.id = task.getId();
        this.jobId = task.getJobId();
        this.status = task.getStatus();
        this.ratedBy = task.getRatedBy();
        this.complexity = task.getComplexity();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public String getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(String ratedBy) {
        this.ratedBy = ratedBy;
    }

    public TaskComplexity getComplexity() {
        return complexity;
    }

    public void setComplexity(TaskComplexity complexity) {
        this.complexity = complexity;
    }

    @Override
    public String toString() {
        return "RateTaskDTO [id=" + id + ", jobId=" + jobId + ", ratedBy=" + ratedBy
                + ", status=" + status + ", complexity=" + complexity + "]";
    }

}
