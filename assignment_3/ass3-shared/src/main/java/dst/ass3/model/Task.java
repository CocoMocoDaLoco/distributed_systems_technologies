package dst.ass3.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Task implements ITask {

	private Long id;
	private Long jobId;
	private TaskStatus status;
	private String ratedBy;
	private TaskComplexity complexity;

	public Task() {
	}

	public Task(Long jobId, TaskStatus status, String ratedBy,
			TaskComplexity complexity) {
		super();
		this.jobId = jobId;
		this.status = status;
		this.ratedBy = ratedBy;
		this.complexity = complexity;
	}

	@Id
	@GeneratedValue
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
		return "Task [id=" + id + ", jobId=" + jobId + ", status=" + status
				+ ", ratedBy=" + ratedBy + ", complexity=" + complexity + "]";
	}

}
