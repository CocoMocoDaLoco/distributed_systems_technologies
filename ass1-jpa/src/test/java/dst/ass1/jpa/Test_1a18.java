package dst.ass1.jpa;

import static junit.framework.Assert.assertTrue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import dst.ass1.AbstractTest;

public class Test_1a18 extends AbstractTest {

	@Test
	public void testClusterAdminAssociationJdbc()
			throws ClassNotFoundException, SQLException {
		String sql = "show columns from User where Field='password' and Type='binary(16)'";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		rs.close();
	}
}
