package dst.ass1.jpa;

import static org.junit.Assert.assertTrue;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.model.IGrid;

public class Test_1a02 extends AbstractTest {

	@Test
	public void testGridConstraint() {
		boolean isConstraint = false;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			IGrid g1 = modelFactory.createGrid();
			g1.setName("grid1");

			IGrid g2 = modelFactory.createGrid();
			g2.setName("grid1");

			em.persist(g1);
			em.persist(g2);
			em.flush();

		} catch (PersistenceException e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				isConstraint = true;
			}

		} finally {
			tx.rollback();
		}

		assertTrue(isConstraint);
	}

	@Test
	public void testGridConstraintJdbc() throws ClassNotFoundException,
			SQLException {
		String sql = "show index from Grid where column_name='name' and non_unique='0'";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		rs.close();

		DatabaseMetaData meta = jdbcConnection.getConnection().getMetaData();
		rs = meta.getColumns(null, null, "Grid", "name");
		assertTrue(rs.next());
		assertTrue(rs.getBoolean("NULLABLE"));
		rs.close();

	}

}
