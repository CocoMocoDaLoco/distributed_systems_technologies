package dst.ass2.ejb.model;

import java.math.BigDecimal;

public interface IPrice {

	public Long getId();

	public void setId(Long id);

	public Integer getNrOfHistoricalJobs();

	public void setNrOfHistoricalJobs(Integer nrOfHistoricalJobs);

	public BigDecimal getPrice();

	public void setPrice(BigDecimal price);

}