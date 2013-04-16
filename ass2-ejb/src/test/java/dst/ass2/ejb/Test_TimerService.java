package dst.ass2.ejb;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dst.ass2.AbstractEJBTest;

public class Test_TimerService extends AbstractEJBTest {

    private final int sleepingTime = 30 * 1000;

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testTimerService() {
        try {
            Thread.sleep(sleepingTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertEquals(1, jdbcTestUtil.getNumberOfFinishedExecutions_FROM_DB());
    }

}
