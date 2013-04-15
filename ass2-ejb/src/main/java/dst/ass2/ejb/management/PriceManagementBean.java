package dst.ass2.ejb.management;

import java.math.BigDecimal;

import dst.ass2.ejb.management.interfaces.IPriceManagementBean;

public class PriceManagementBean implements IPriceManagementBean {
	
	// TODO
	
	@Override
	public BigDecimal getPrice(Integer nrOfHistoricalJobs) {
		// TODO
		return new BigDecimal(0.0);
	}

	@Override
	public void setPrice(Integer nrOfHistoricalJobs, BigDecimal price) {
		// TODO
	}
	
	
	@Override
	public void clearCache() {
		// TODO
	}
}
