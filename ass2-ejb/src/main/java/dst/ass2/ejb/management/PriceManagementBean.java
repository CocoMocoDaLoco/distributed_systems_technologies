package dst.ass2.ejb.management;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.annotation.PostConstruct;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Local;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import dst.ass2.ejb.management.interfaces.IPriceManagementBean;
import dst.ass2.ejb.model.impl.Price;

/* Singleton was chosen because we have a central state
 * which needs to be initialized at startup. Both stateless and stateful
 * session bean types are not suitable for this scenario.
 */

@Local(IPriceManagementBean.class)
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@Singleton
@Startup
public class PriceManagementBean implements IPriceManagementBean {

    private final ConcurrentSkipListSet<Price> prices = new ConcurrentSkipListSet<Price>();

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void postConstruct() {
        Query query = entityManager.createQuery("from " + Price.class.getName());
        prices.addAll(query.getResultList());
    }

    @Override
    public BigDecimal getPrice(Integer nrOfHistoricalJobs) {
        Price that = new Price();
        that.setNrOfHistoricalJobs(nrOfHistoricalJobs);

        Price price = prices.higher(that);
        if (price == null) {
            return new BigDecimal(0);
        }

        return price.getPrice();
    }

    @Override
    public void setPrice(Integer nrOfHistoricalJobs, BigDecimal price) {
        Price p = new Price();
        p.setNrOfHistoricalJobs(nrOfHistoricalJobs);
        p.setPrice(price);

        prices.add(p);

        entityManager.persist(p);
    }


    @Override
    public void clearCache() {
        prices.clear();

        /* Not sure what this method is supposed to do. Just clear the cache (which we do now),
         * or also remove entities from the DB? */
    }
}
