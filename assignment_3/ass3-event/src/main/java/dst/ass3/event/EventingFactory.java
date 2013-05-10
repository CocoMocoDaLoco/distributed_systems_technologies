package dst.ass3.event;

import dst.ass3.model.ITask;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;
import dst.ass3.dto.TaskDTO;

/**
 * Factory for instantiating objects used in the eventing tests
 * (interfaces IEventProcessing and ITask).
 */
public class EventingFactory {


	public static IEventProcessing getInstance() {

		// TODO

		return null;
	}

	public static ITask createTask(Long id, Long jobId, TaskStatus status,
			String ratedBy, TaskComplexity complexity) {
		return new TaskDTO(id, jobId, status, ratedBy, complexity);
	}
}
