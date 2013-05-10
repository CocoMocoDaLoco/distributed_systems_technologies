package dst.ass2.ejb;

import static dst.ass2.ejb.util.EJBUtils.lookup;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dst.ass1.jpa.util.JdbcHelper;
import dst.ass2.AbstractEJBTest;
import dst.ass2.ejb.dto.AuditLogDTO;
import dst.ass2.ejb.session.JobManagementBean;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.interfaces.IJobManagementBean;

public class Test_AuditInterceptor extends AbstractEJBTest {

    @Before
    public void setUp() {
        managementBean.clearPriceCache();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testAuditLog() {

        // get all available grid-ids from db
        List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();

        assertEquals(2, gridIds.size());

        // add prices to management bean
        managementBean_addPrices();

        // add jobs to job management bean
        try {
            jobmanagementBean_addJobs(gridIds);
        } catch (Exception e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        }

        // check the produced audit logs
        List<AuditLogDTO> auditLogs = managementBean.getAuditLogs();
        assertEquals(4, auditLogs.size());

        List<AuditLogDTO> temp = getAuditLogsForMethod("login", auditLogs);
        assertEquals(1, temp.size());
        AuditLogDTO audit = temp.get(0);

        assertNotNull(audit.getParameters());
        assertEquals(2, audit.getParameters().size());
        assertNotNull(audit.getInvocationTime());

        temp = getAuditLogsForMethod("addJob", auditLogs);
        assertEquals(2, temp.size());

        for (AuditLogDTO auditLogDTO : temp) {
            assertNotNull(auditLogDTO.getParameters());
            assertEquals(4, auditLogDTO.getParameters().size());
            assertNotNull(auditLogDTO.getInvocationTime());
        }

        temp = getAuditLogsForMethod("submitAssignments", auditLogs);
        assertEquals(1, temp.size());
        audit = temp.get(0);

        assertNotNull(audit.getParameters());
        assertEquals(0, audit.getParameters().size());
        assertNotNull(audit.getInvocationTime());
    }

    @Test
    public void testAuditLog_With_Exception() {

        try {
            // get all available grid-ids from db
            List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();

            assertEquals(2, gridIds.size());

            // add prices to management bean
            managementBean_addPrices();

            IJobManagementBean jobManagementBean = lookup(ctx,
                    JobManagementBean.class);

            jobManagementBean.login("franz", "liebe");

            // add a job which needs more Cpus than available and therefore
            // triggers an AssignmentException
            try {
                List<String> params = new ArrayList<String>();

                int maxCapacity = JdbcHelper.getAvailableCpuCount(
                        jdbcConnection, String.valueOf(gridIds.get(0)));

                jobManagementBean.addJob(gridIds.get(0), maxCapacity + 1,
                        "workflow5", params);

                fail("Wrong calculation @ JobManagementBean, grid does not have enough capacity to process this job. The test cannot be completed!");
            } catch (AssignmentException e) {
                // expected Exception - nothing to do
            } catch (SQLException e) {
                e.printStackTrace();
                fail(String
                        .format("Unexpected Exception: %s !", e.getMessage()));
            }

            List<AuditLogDTO> auditLogs = managementBean.getAuditLogs();
            assertEquals(2, auditLogs.size());

            List<AuditLogDTO> temp = getAuditLogsForResult(
                    AssignmentException.class.getName(), auditLogs);
            assertEquals(1, temp.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        }

    }

    public List<AuditLogDTO> getAuditLogsForMethod(String method,
            List<AuditLogDTO> audits) {
        List<AuditLogDTO> ret = new ArrayList<AuditLogDTO>();

        for (AuditLogDTO audit : audits) {
            if (method.equals(audit.getMethod()))
                ret.add(audit);
        }

        return ret;
    }

    public List<AuditLogDTO> getAuditLogsForResult(String result,
            List<AuditLogDTO> audits) {
        List<AuditLogDTO> ret = new ArrayList<AuditLogDTO>();

        for (AuditLogDTO audit : audits) {
            if (audit.getResult() != null
                    && audit.getResult().startsWith(result))
                ret.add(audit);
        }

        return ret;
    }

}
