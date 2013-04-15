package dst.ass2.ejb.dto;

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IExecution;

@XmlRootElement(name = "stats")
public class StatisticsDTO {

	private String name;
	private List<ExecutionDTO> executions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<ExecutionDTO> getExecutions() {
		return executions;
	}

	public void setExecutions(List<ExecutionDTO> executions) {
		this.executions = executions;
	}

	public void addExecution(IExecution e) {
		if (executions == null)
			executions = new LinkedList<ExecutionDTO>();
		int cpus = 0;
		for (IComputer c : e.getComputers()) {
			cpus += c.getCpus();
		}
		ExecutionDTO edto = new ExecutionDTO(e.getStart(), e.getEnd(), cpus);
		executions.add(edto);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Grid Name: ");
		sb.append(name);
		sb.append("\n");
		for (ExecutionDTO e : executions) {
			sb.append(e.toString());
			sb.append("\n");
		}
		return sb.toString();
	}

}
