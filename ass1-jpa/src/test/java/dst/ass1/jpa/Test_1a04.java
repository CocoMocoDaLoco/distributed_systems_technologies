package dst.ass1.jpa;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IGrid;

public class Test_1a04 extends AbstractTest {

	private Long computer1Id;

	@Test
	public void testComputerConstraint() {
		boolean isConstraint = false;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			IComputer c1 = (new DAOFactory((Session) em.getDelegate()))
					.getComputerDAO().findById(computer1Id);
			c1.setName("computer2");
			em.persist(c1);
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
	public void testComputerConstraintJdbc() throws ClassNotFoundException,
			SQLException {
		String sql = "show index from Computer where column_name='name' and non_unique='0'";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		rs.close();
	}

	public void setUpDatabase() {

		ICluster cluster1 = modelFactory.createCluster();
		IAdmin admin = modelFactory.createAdmin();
		IGrid grid = modelFactory.createGrid();
		cluster1.setAdmin(admin);
		cluster1.setName("cluster1");
		cluster1.setGrid(grid);

		IComputer c1 = modelFactory.createComputer();
		c1.setName("computer1");
		c1.setCluster(cluster1);

		IComputer c2 = modelFactory.createComputer();
		c2.setName("computer2");
		c2.setCluster(cluster1);

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			em.persist(grid);
			em.persist(admin);
			em.persist(cluster1);
			em.persist(c1);
			em.persist(c2);
			tx.commit();

			computer1Id = c1.getId();
		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}

	}

}
