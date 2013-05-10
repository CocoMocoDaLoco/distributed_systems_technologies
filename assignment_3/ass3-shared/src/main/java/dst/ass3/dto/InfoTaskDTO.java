package dst.ass3.dto;

import java.io.Serializable;

public class InfoTaskDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long taskId;

	public InfoTaskDTO() {
	}

	public InfoTaskDTO(Long taskId) {
		super();
		this.taskId = taskId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

}
