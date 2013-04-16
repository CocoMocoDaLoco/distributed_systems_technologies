package dst.ass2.ejb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dst.ass2.AbstractEJBTest;

public class Test_GeneralManagementBean extends AbstractEJBTest {

    @Before
    public void setUp() {
        managementBean.clearPriceCache();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAddPrice() {
        managementBean.addPrice(new Integer(0), new BigDecimal(50));
        managementBean.addPrice(new Integer(1), new BigDecimal(45));

        assertEquals(2, jdbcTestUtil.getNumberOfPrices_FROM_DB());

        assertTrue(jdbcTestUtil.isPrice_IN_DB(Integer.valueOf(0),
                BigDecimal.valueOf(50)));
        assertTrue(jdbcTestUtil.isPrice_IN_DB(Integer.valueOf(1),
                BigDecimal.valueOf(45)));
    }

    @Test
    public void testAddPrice_Cache() {
        managementBean.addPrice(new Integer(0), new BigDecimal(50));

        assertEquals(1, jdbcTestUtil.getNumberOfPrices_FROM_DB());

        assertTrue(jdbcTestUtil.isPrice_IN_DB(Integer.valueOf(0),
                BigDecimal.valueOf(50)));

        jdbcTestUtil.removeAllPrices_FROM_DB();

        // add same price a second time => should be cached and therefore not
        // stored in the db
        managementBean.addPrice(new Integer(0), new BigDecimal(50));

        assertEquals(0, jdbcTestUtil.getNumberOfPrices_FROM_DB());

        // clear cache
        managementBean.clearPriceCache();

        // add same price to cleared cache => should be stored in the db
        managementBean.addPrice(new Integer(0), new BigDecimal(50));

        assertEquals(1, jdbcTestUtil.getNumberOfPrices_FROM_DB());

        assertTrue(jdbcTestUtil.isPrice_IN_DB(Integer.valueOf(0),
                BigDecimal.valueOf(50)));
    }
}
