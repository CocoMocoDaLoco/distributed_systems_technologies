package dst.ass1.jpa;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a16 extends AbstractTest {
	
	@Test
	public void testGridClusterAssociationJdbc() throws ClassNotFoundException,
			SQLException {
		assertTrue(JdbcHelper.isColumnInTable("Computer", "cluster_id",
				jdbcConnection));
		assertFalse(JdbcHelper.isColumnInTable("Cluster", "computer_id",
				jdbcConnection));
		assertTrue(JdbcHelper.isIndex("Computer", "cluster_id", true,
				jdbcConnection));
	}

}
