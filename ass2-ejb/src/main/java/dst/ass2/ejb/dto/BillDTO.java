package dst.ass2.ejb.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class BillDTO implements Serializable {
    private static final long serialVersionUID = 1577495607705041680L;

    private List<BillPerJob> bills;
    private BigDecimal totalPrice;
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<BillPerJob> getBills() {
        return bills;
    }

    public void setBills(List<BillPerJob> bills) {
        this.bills = bills;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public class BillPerJob implements Serializable {
        private static final long serialVersionUID = 8656991004468599034L;

        private Long jobId;
        private Integer numberOfComputers;
        private BigDecimal setupCosts;
        private BigDecimal executionCosts;
        private BigDecimal jobCosts;

        public BigDecimal getExecutionCosts() {
            return executionCosts;
        }

        public void setExecutionCosts(BigDecimal executionCosts) {
            this.executionCosts = executionCosts;
        }

        public Long getJobId() {
            return jobId;
        }

        public void setJobId(Long jobId) {
            this.jobId = jobId;
        }

        public Integer getNumberOfComputers() {
            return numberOfComputers;
        }

        public void setNumberOfComputers(Integer numberOfComputers) {
            this.numberOfComputers = numberOfComputers;
        }

        public BigDecimal getSetupCosts() {
            return setupCosts;
        }

        public void setSetupCosts(BigDecimal setupCosts) {
            this.setupCosts = setupCosts;
        }

        public BigDecimal getJobCosts() {
            return jobCosts;
        }

        public void setJobCosts(BigDecimal jobCosts) {
            this.jobCosts = jobCosts;
        }

        @Override
        public String toString() {
            return "BillPerJob [jobId=" + jobId + ", numberOfComputers="
                    + numberOfComputers + ", setupCosts=" + setupCosts
                    + ", executionCosts=" + executionCosts + ", jobCosts="
                    + jobCosts + "]";
        }

    }

    @Override
    public String toString() {
        return "BillDTO [bills=" + bills + ", totalPrice=" + totalPrice
                + ", username=" + username + "]";
    }

}
