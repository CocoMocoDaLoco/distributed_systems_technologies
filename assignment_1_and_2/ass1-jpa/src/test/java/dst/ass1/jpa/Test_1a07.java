package dst.ass1.jpa;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a07 extends AbstractTest {

	@Test
	public void testEnvironmentParamsJdbc() throws ClassNotFoundException,
			SQLException {
		assertTrue(JdbcHelper.isTable("environment_params", jdbcConnection));
	}
}
