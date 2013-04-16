package dst.ass2.util;

import static dst.ass2.util.SQLQueries.*;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dst.ass1.jpa.util.JdbcConnection;
import dst.ass1.jpa.util.JdbcHelper;

public class JDBCTestUtil {

    private JdbcConnection jdbcConnection;

    public JDBCTestUtil(JdbcConnection jdbcConnection) {
        this.jdbcConnection = jdbcConnection;
    }

    private Statement createStatement() throws SQLException {
        return jdbcConnection.getConnection().createStatement();
    }

    private void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                /*
                 * Note: When a Statement object is closed, its current
                 * ResultSet object, if one exists, is also closed.
                 */
                statement.close();
            } catch (SQLException e) {
            }
        }
    }

    public List<Long> getAllGridIds_FROM_DB() {
        List<Long> gridIds = new ArrayList<Long>();
        Statement stmt = null;

        try {
            stmt = createStatement();
            ResultSet rs = stmt.executeQuery(SQL_GET_ALL_GRID_IDS);

            while (rs.next())
                gridIds.add(rs.getLong(1));

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(stmt);
        }

        return gridIds;
    }

    public List<Long> getPaidJobsForUser_FROM_DB(String user) {
        List<Long> jobIds = new ArrayList<Long>();

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_GET_ALL_PAID_JOBS_FOR_USER);
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                jobIds.add(rs.getLong("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return jobIds;
    }

    public int getNumberOfPrices_FROM_DB() {
        int ret = 0;

        Statement stmt = null;

        try {
            stmt = createStatement();
            ResultSet rs = stmt.executeQuery(SQL_COUNT_PRICES);

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(stmt);
        }

        return ret;
    }

    public boolean isPrice_IN_DB(Integer nrOfHistoricalJobs, BigDecimal price) {
        boolean ret = false;

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_IS_PRICE_AVAILABLE);
            pstmt.setInt(1, nrOfHistoricalJobs.intValue());
            pstmt.setBigDecimal(2, price);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);

                if (count == 1)
                    ret = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }

    public void removeAllPrices_FROM_DB() {
        truncateTable_IN_DB(SQL_PRICE_TABLE_NAME);
    }

    public int getNumberOfJobs_FROM_DB() {
        int ret = -1;

        Statement stmt = null;

        try {
            stmt = createStatement();
            ResultSet rs = stmt.executeQuery(SQL_COUNT_JOBS);

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(stmt);
        }

        return ret;
    }

    public List<JobHelperDTO> getAllJobs_FROM_DB() {
        List<JobHelperDTO> jobs = new ArrayList<JobHelperDTO>();

        Statement stmt = null;

        try {
            stmt = createStatement();
            ResultSet rs = stmt.executeQuery(SQL_GET_ALL_JOBS);

            while (rs.next()) {
                // j.id, env.workflow, ex.start, u.username
                Long id = rs.getLong("id");
                String workflow = rs.getString("workflow");
                Date start = rs.getDate("start");
                String username = rs.getString("username");

                jobs.add(new JobHelperDTO(id, workflow, start, username, null));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(stmt);
        }

        for (JobHelperDTO job : jobs) {
            // get workflow parameter for job
            List<String> parameters = getParametersForJob_FROM_DB(job.getId());
            job.setParams(parameters);
        }

        return jobs;
    }

    public void removeAllJobs_FROM_DB() {
        truncateTable_IN_DB(SQL_JOB_TABLE_NAME);
    }

    private void truncateTable_IN_DB(String table) {
        Statement stmt = null;
        try {
            stmt = createStatement();
            stmt.addBatch("SET FOREIGN_KEY_CHECKS=0");
            stmt.addBatch("truncate table " + table);
            stmt.addBatch("SET FOREIGN_KEY_CHECKS=1");
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(stmt);
        }
    }

    private List<String> getParametersForJob_FROM_DB(Long jobId) {
        List<String> parameters = new ArrayList<String>();

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_GET_PARAMS_FOR_JOB);
            pstmt.setLong(1, jobId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next())
                parameters.add(rs.getString("params"));

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return parameters;
    }

    public int getNumberOfFinishedExecutions_FROM_DB() {
        int ret = 0;

        Statement stmt = null;

        try {
            stmt = createStatement();
            ResultSet rs = stmt.executeQuery(SQL_COUNT_FINISHED_EXECUTION);

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(stmt);
        }

        return ret;
    }

    public int getNumberOfAdmins_FROM_DB(String city, String street,
            String zipCode, String firstName, String lastName) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            int type = JdbcHelper.getInheritanceType(jdbcConnection, "Person");
            switch (type) {
            case 0:
                pstmt = jdbcConnection.getConnection().prepareStatement(
                        SQL_COUNT_ADMIN_1);
                break;
            case 1:
                pstmt = jdbcConnection.getConnection().prepareStatement(
                        SQL_COUNT_ADMIN_2);
                break;
            default:
                fail("Unknown inheritance type is used!");
            }

            pstmt.setString(1, city);
            pstmt.setString(2, street);
            pstmt.setString(3, zipCode);
            pstmt.setString(4, firstName);
            pstmt.setString(5, lastName);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }

    public int getNumberOfClusters_FROM_DB(String name) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_COUNT_Cluster);
            pstmt.setString(1, name);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }

    public int getNumberOfComputers_FROM_DB(int cpus, String location,
            String name) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_COUNT_COMPUTER);
            pstmt.setInt(1, cpus);
            pstmt.setString(2, location);
            pstmt.setString(3, name);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }

    public int getNumberOfEnvrionments_FROM_DB(String workflow) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_COUNT_ENVIRONMENT);
            pstmt.setString(1, workflow);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }

    public int getNumberOfExecutions_FROM_DB() {
        int ret = 0;

        Statement stmt = null;

        try {
            stmt = createStatement();
            ResultSet rs = stmt.executeQuery(SQL_COUNT_EXECUTION);

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(stmt);
        }

        return ret;
    }

    public int getNumberOfGrids_FROM_DB(BigDecimal costsPerMinute,
            String location, String name) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_COUNT_GRID);
            pstmt.setBigDecimal(1, costsPerMinute);
            pstmt.setString(2, location);
            pstmt.setString(3, name);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }

    public int getNumberOfJobs_FROM_DB(boolean isPaid) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_COUNT_JOB);
            pstmt.setBoolean(1, isPaid);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }

    public int getNumberOfMemberships_FROM_DB(Double discount) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            pstmt = jdbcConnection.getConnection().prepareStatement(
                    SQL_COUNT_MEMBERSHIP);
            pstmt.setDouble(1, discount);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;

    }

    public int getNumberOfUsers_FROM_DB(String city, String street,
            String zipCode, String firstName, String lastName, String bankCode,
            String accountNo, String userName, byte[] password) {
        int ret = 0;

        PreparedStatement pstmt = null;

        try {
            int type = JdbcHelper.getInheritanceType(jdbcConnection, "Person");
            switch (type) {
            case 0:
                pstmt = jdbcConnection.getConnection().prepareStatement(
                        SQL_COUNT_USER_1);
                break;
            case 1:
                pstmt = jdbcConnection.getConnection().prepareStatement(
                        SQL_COUNT_USER_2);
                break;
            default:
                fail("Unknown inheritance type is used!");
            }

            pstmt.setString(1, city);
            pstmt.setString(2, street);
            pstmt.setString(3, zipCode);
            pstmt.setString(4, firstName);
            pstmt.setString(5, lastName);
            pstmt.setString(6, bankCode);
            pstmt.setString(7, accountNo);
            pstmt.setString(8, userName);
            pstmt.setBytes(9, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next())
                ret = rs.getInt(1);

        } catch (SQLException e) {
            e.printStackTrace();
            fail(String.format("Unexpected Exception: %s !", e.getMessage()));
        } finally {
            closeStatement(pstmt);
        }

        return ret;
    }
}
