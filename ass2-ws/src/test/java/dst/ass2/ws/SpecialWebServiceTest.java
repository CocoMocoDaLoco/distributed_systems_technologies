package dst.ass2.ws;

import static dst.ass2.ejb.util.EJBUtils.lookup;
import static dst.ass2.ws.MiscUtils.filterAuditLogs;
import static dst.ass2.ws.MiscUtils.filterExecutionDtos;
import static dst.ass2.ws.MiscUtils.validateAuditLog;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.NoSuchEJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import dst.ass1.jpa.util.JdbcConnection;
import dst.ass1.jpa.util.JdbcHelper;
import dst.ass2.ejb.dto.AssignmentDTO;
import dst.ass2.ejb.dto.AuditLogDTO;
import dst.ass2.ejb.dto.BillDTO;
import dst.ass2.ejb.session.GeneralManagementBean;
import dst.ass2.ejb.session.JobManagementBean;
import dst.ass2.ejb.session.TestingBean;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.exception.WebServiceException;
import dst.ass2.ejb.session.interfaces.IGeneralManagementBean;
import dst.ass2.ejb.session.interfaces.IJobManagementBean;
import dst.ass2.ejb.session.interfaces.IJobStatisticsBean;
import dst.ass2.ejb.session.interfaces.ITestingBean;
import dst.ass2.ejb.ws.Constants;
import dst.ass2.ejb.ws.IGetStatsRequest;
import dst.ass2.ejb.ws.IGetStatsResponse;
import dst.ass2.ejb.ws.WSRequestFactory;

/**
 * Test scenario for the service implementation.
 */
public class SpecialWebServiceTest {
    static Long gridId;
    static Context ctx;
    long startTime = System.currentTimeMillis();

    private static JdbcConnection jdbcConnection;

    @BeforeClass
    public static void before() throws Exception {
        ctx = new InitialContext();
        jdbcConnection = new JdbcConnection();
        JdbcHelper.cleanTables(jdbcConnection);
    }

    @AfterClass
    public static void after() throws Exception {
        ctx.close();
        if (jdbcConnection != null)
            jdbcConnection.disconnect();

    }

    @Test
    public void testScenario() throws Exception {
        // Wait a little bit to reduce possible impacts of other tests
        Thread.sleep(1000);

        IGeneralManagementBean generalManagementBean = lookup(ctx,
                GeneralManagementBean.class);

        init();

        // Add some prices
        addPriceSteps(generalManagementBean);

        // Schedule some jobs
        addJobs();

        // Schedule more jobs
        addAdditionalJobs();

        // Wait for the result
        Thread.sleep(5000);

        // Retrieve the bill
        getBill(generalManagementBean);

        // Look at the audit log
        getAudits(generalManagementBean);

        // Do a web service call
        webService();
    }

    private void init() {
        try {
            ITestingBean testingBean = lookup(ctx, TestingBean.class);
            testingBean.insertTestData();
            gridId = JdbcHelper.getGridIds(jdbcConnection).get(0);
        } catch (Exception e) {
            // May occur if test data has already been inserted
        }
    }

    private void addPriceSteps(IGeneralManagementBean generalManagementBean) {
        // Add some price steps
        generalManagementBean.addPrice(0, new BigDecimal(10));
        generalManagementBean.addPrice(1, new BigDecimal(5));
        generalManagementBean.addPrice(2, new BigDecimal(2));
        for (int i = 3; i < 10; i++) {
            generalManagementBean.addPrice(i, new BigDecimal(1));
        }
    }

    private void addJobs() throws NamingException {
        IJobManagementBean jobManagementBean = lookup(ctx,
                JobManagementBean.class);

        // Login with invalid username and password
        try {
            jobManagementBean.login("", "");
            throw new RuntimeException(
                    AssignmentException.class.getSimpleName() + " expected");
        } catch (AssignmentException e) {
            // expected
        }

        // Login with valid username and password
        try {
            jobManagementBean.login("hansi", "pw");
        } catch (AssignmentException e) {
            fail(ExceptionUtils.getMessage(e));
        }

        // Add first job for 4 CPUs
        try {
            jobManagementBean.addJob(gridId, 4, "1",
                    Collections.<String> emptyList());
        } catch (AssignmentException e) {
            fail(ExceptionUtils.getMessage(e));
        }

        // Add second job for 4 CPUs
        try {
            jobManagementBean.addJob(gridId, 4, "2", Arrays.asList("a", "b"));
        } catch (AssignmentException e) {
            fail(ExceptionUtils.getMessage(e));
        }

        List<AssignmentDTO> assignments = jobManagementBean.getCache();
        assertEquals("Exactly 2 assignments expected", 2, assignments.size());

        // Starting jobs
        try {
            jobManagementBean.submitAssignments();
        } catch (AssignmentException e) {
            fail(ExceptionUtils.getMessage(e));
        }

        // Resubmit
        try {
            jobManagementBean.submitAssignments();
            fail(NoSuchEJBException.class.getSimpleName() + " expected");
        } catch (NoSuchEJBException e) {
            // Expected since the previous call of submitAssignment() should
            // discard the bean!!!
        } catch (AssignmentException e) {
            fail(ExceptionUtils.getMessage(e));
        }
    }

    private void addAdditionalJobs() throws NamingException,
            AssignmentException {
        IJobManagementBean jobManagementBean = lookup(ctx,
                JobManagementBean.class);

        // Login with another valid username and password
        jobManagementBean.login("franz", "liebe");

        // Add job for 2147483647 CPUs
        try {
            jobManagementBean.addJob(gridId, Integer.MAX_VALUE, "max",
                    Collections.<String> emptyList());
            fail(AssignmentException.class.getSimpleName() + " expected");
        } catch (AssignmentException e) {
            // expected
        }

        // Clear shopping cart
        jobManagementBean.removeJobsForGrid(gridId);
        assertTrue("The shopping cart must be empty", jobManagementBean
                .getCache().isEmpty());

        // Start jobs
        jobManagementBean.submitAssignments();
    }

    private void getBill(IGeneralManagementBean generalManagementBean)
            throws Exception {
        // Request bill for hansi
        Future<BillDTO> future = generalManagementBean.getBillForUser("hansi");
        BillDTO bill = future.get();

        // Verify price
        assertEquals(new BigDecimal("150.30"),
                bill.getTotalPrice());
    }

    private void getAudits(IGeneralManagementBean generalManagementBean)
            throws InterruptedException {
        List<AuditLogDTO> audits = filterAuditLogs(
                generalManagementBean.getAuditLogs(), startTime);
        for (int i = 0; audits.size() != 11 && i < 10; i++) {
            // If necessary, give some more time to retrieve the correct amount
            // of audit logs
            Thread.sleep(1000);
            audits = filterAuditLogs(generalManagementBean.getAuditLogs(),
                    startTime);
        }
        assertEquals("Expected 11 audit log entries", 11, audits.size());

        validateAuditLog(audits.get(0), "login",
                AssignmentException.class.getSimpleName(), "", "");
        validateAuditLog(audits.get(1), "login", null, "hansi", "pw");
        validateAuditLog(audits.get(2), "addJob", null, gridId.toString(), "4",
                "1", "[]");
        validateAuditLog(audits.get(3), "addJob", null, gridId.toString(), "4",
                "2", "[a, b]");
        validateAuditLog(audits.get(4), "getCache", "2");
        validateAuditLog(audits.get(5), "submitAssignments", null);
        validateAuditLog(audits.get(6), "login", null, "franz", "liebe");
        validateAuditLog(audits.get(7), "addJob",
                AssignmentException.class.getSimpleName(), gridId.toString(),
                "2147483647", "max", "[]");
        validateAuditLog(audits.get(8), "removeJobsForGrid", null,
                gridId.toString());
        validateAuditLog(audits.get(9), "getCache", "[]");
        validateAuditLog(audits.get(10), "submitAssignments", null);
    }

    private void webService() {
        // Create Web service client
        IJobStatisticsBean service = WebServiceUtils.getServiceProxy(
                IJobStatisticsBean.class, Constants.NAMESPACE,
                Constants.SERVICE_NAME, Constants.SERVICE_WSDL_URL);
        WSRequestFactory factory = new WSRequestFactory();

        // Retrieving stats for grid 1
        try {
            IGetStatsRequest request1 = factory.createGetStatsRequest();
            request1.setMaxExecutions(10);
            IGetStatsResponse stats = service.getStatisticsForGrid(request1,
                    "grid1");
            assertEquals("The name of the statistics must be 'grid1'", "grid1",
                    stats.getStatistics().getName());
            assertEquals(
                    "There must be 2 executions",
                    2,
                    filterExecutionDtos(stats.getStatistics().getExecutions(),
                            startTime).size());
        } catch (WebServiceException e1) {
            fail("Unexpected exception when requesting statistics for grid 'grid1': "
                    + e1);
        }

        // Retrieving stats for grid 2
        try {
            IGetStatsRequest request2 = factory.createGetStatsRequest();
            request2.setMaxExecutions(10);
            IGetStatsResponse stats = service.getStatisticsForGrid(request2,
                    "grid2");
            assertEquals("The name of the statistics must be 'grid2'", "grid2",
                    stats.getStatistics().getName());
            assertEquals(
                    "There must be 0 executions",
                    0,
                    filterExecutionDtos(stats.getStatistics().getExecutions(),
                            startTime).size());
        } catch (WebServiceException e1) {
            fail("Unexpected exception when requesting statistics for grid 'grid2': "
                    + e1);
        }

        // Retrieving stats for grid Unknown
        try {
            IGetStatsRequest request3 = factory.createGetStatsRequest();
            request3.setMaxExecutions(10);
            service.getStatisticsForGrid(request3, "grid3");
            fail("Exception expected when requesting statistics for unknown grid.");
        } catch (Exception e) {
            // expected
        }
    }
}
