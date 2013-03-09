package dst.ass1.jpa;

import static junit.framework.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a13 extends AbstractTest {

	@Test
	public void testExecutionComputerAssociationJdbc()
			throws ClassNotFoundException, SQLException {
		assertTrue(JdbcHelper.isTable("execution_computer", jdbcConnection));
		assertTrue(JdbcHelper.isColumnInTable("execution_computer",
				"executions_id", jdbcConnection));
		assertTrue(JdbcHelper.isColumnInTable("execution_computer",
				"computers_id", jdbcConnection));
	}

}
