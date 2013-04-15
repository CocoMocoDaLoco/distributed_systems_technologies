package dst.ass2.ejb.dto;

import java.util.Date;

public class ExecutionDTO {
	
	private Date startDate;
	private Date endDate;
	private int cpus;

	public ExecutionDTO() {}
	
	public ExecutionDTO(Date startDate, Date endDate, int cpus) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.cpus = cpus;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public int getCpus() {
		return cpus;
	}
	public void setCpus(int cpus) {
		this.cpus = cpus;
	}
	
	@Override
	public String toString() {
		return startDate.toString()+" -- "+endDate.toString()+" ("+cpus+")";
	}
	
}
