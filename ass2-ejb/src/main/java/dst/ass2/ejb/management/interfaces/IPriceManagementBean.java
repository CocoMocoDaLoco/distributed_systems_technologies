package dst.ass2.ejb.management.interfaces;

import java.math.BigDecimal;

import javax.ejb.Local;

@Local
public interface IPriceManagementBean {

    /**
     * @param nrOfHistoricalJobs
     * @return the price for the given number of historical jobs. If there was
     *         no price for this number of jobs specified it returns 0.
     */
    public BigDecimal getPrice(Integer nrOfHistoricalJobs);

    /**
     * Creates a price-step for the given number of historical jobs.
     * 
     * @param nrOfHistoricalJobs
     * @param price
     */
    public void setPrice(Integer nrOfHistoricalJobs, BigDecimal price);

    /**
     * Clears the cached price-steps.
     */
    public void clearCache();

}
