package dst.ass1.jpa;

import static junit.framework.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import dst.ass1.AbstractTest;

public class Test_2d extends AbstractTest {
	
	@Test
	public void testUserPasswordIdx() throws SQLException, ClassNotFoundException {
		String sql = "show index from User where column_name='password' and non_unique=1";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		rs.close();
	}
}
