package dst.ass3.dto;

import java.io.Serializable;

public class NewTaskDTO implements Serializable {

	private static final long serialVersionUID = 843972285375484461L;
	private Long jobId;

	public NewTaskDTO() {
	}

	public NewTaskDTO(Long jobId) {
		this.jobId = jobId;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

}
