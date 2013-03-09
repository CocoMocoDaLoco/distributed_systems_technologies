package dst.ass1.jpa;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a09 extends AbstractTest {
	
	@Test
	public void testMembershipsJdbc() throws ClassNotFoundException, SQLException {
		assertTrue(JdbcHelper.isTable("Membership", jdbcConnection));
		assertTrue(JdbcHelper.isColumnInTable("Membership", "grid_id", jdbcConnection));
		assertTrue(JdbcHelper.isColumnInTable("Membership", "user_id", jdbcConnection));
		assertFalse(JdbcHelper.isColumnInTable("Grid", "membership_id", jdbcConnection));
		assertFalse(JdbcHelper.isColumnInTable("User", "membership_id", jdbcConnection));
		
	}
}
