package dst.ass1.jpa;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a17 extends AbstractTest{

	@Test
	public void testClusterAdminAssociationJdbc()
			throws ClassNotFoundException, SQLException {
		assertTrue(JdbcHelper.isIndex("Cluster", "admin_id", true,
				jdbcConnection));
		assertTrue(JdbcHelper.isColumnInTable("Cluster", "admin_id",
				jdbcConnection));
		assertFalse(JdbcHelper.isColumnInTable("Admin", "cluster_id",
				jdbcConnection));
	}
}
