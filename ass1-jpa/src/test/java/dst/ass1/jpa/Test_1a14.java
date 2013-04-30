package dst.ass1.jpa;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a14 extends AbstractTest {

	@Test
	public void testGridClusterAssociationJdbc() throws ClassNotFoundException,
			SQLException {
		assertFalse(JdbcHelper.isColumnInTable("Grid", "cluster_id",
				jdbcConnection));
		assertTrue(JdbcHelper.isColumnInTable("Cluster", "grid_id",
				jdbcConnection));
		assertTrue(JdbcHelper.isIndex("Cluster", "grid_id", true,
				jdbcConnection));
	}

}
