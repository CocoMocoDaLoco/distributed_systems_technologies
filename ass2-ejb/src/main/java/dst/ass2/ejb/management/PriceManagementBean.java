package dst.ass2.ejb.management;

import java.math.BigDecimal;

import javax.annotation.PostConstruct;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import dst.ass2.ejb.management.interfaces.IPriceManagementBean;
import dst.ass2.ejb.model.IPrice;

/* Singleton was chosen because we have a central state
 * which needs to be initialized at startup. Both stateless and stateful
 * session bean types are not suitable for this scenario.
 */

@Local(IPriceManagementBean.class)
@Singleton
@Startup
public class PriceManagementBean implements IPriceManagementBean {

    //    private EntityManager entityManager;

    @PostConstruct
    public void postConstruct() {
        //        EntityManagerFactory emf = Persistence.createEntityManagerFactory("dst");
        //        entityManager = emf.createEntityManager();
        //        Query query = entityManager.createQuery("from " + Price.class.getName());
        //        prices.addAll(query.getResultList());

        /* TODO */
    }

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

    @Entity
    private static class Price implements IPrice, Comparable<Price> {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private long id;
        private int nrOfHistoricalJobs;
        private BigDecimal price;

        @Override
        public Long getId() {
            return id;
        }

        @Override
        public void setId(Long id) {
            this.id = id;
        }

        @Override
        public Integer getNrOfHistoricalJobs() {
            return nrOfHistoricalJobs;
        }

        @Override
        public void setNrOfHistoricalJobs(Integer nrOfHistoricalJobs) {
            this.nrOfHistoricalJobs = nrOfHistoricalJobs;
        }

        @Override
        public BigDecimal getPrice() {
            return price;
        }

        @Override
        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        @Override
        public int compareTo(Price that) {
            return nrOfHistoricalJobs - that.nrOfHistoricalJobs;
        }

    }
}
