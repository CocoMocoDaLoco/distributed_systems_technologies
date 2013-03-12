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
import dst.ass1.jpa.model.IGrid;

public class Test_1a03 extends AbstractTest {

	private Long clusterId;
	
	@Test
	public void testClusterNameConstraint() {
		boolean isConstraint = false;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			ICluster cluster = (new DAOFactory((Session) em.getDelegate()))
					.getClusterDAO().findById(clusterId);
			cluster.setName("cluster2");
			em.persist(cluster);
			em.flush();

		} catch (PersistenceException e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				isConstraint = true;
			}

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		assertTrue(isConstraint);
	}

	@Test
	public void testClusterNameConstraintJdbc() throws ClassNotFoundException,
			SQLException {
		String sql = "show index from Cluster where column_name='name' and non_unique='0'";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		rs.close();
	}

	protected void setUpDatabase() {
		ICluster cluster1 = modelFactory.createCluster();
		ICluster cluster2 = modelFactory.createCluster();
		IAdmin admin = modelFactory.createAdmin();
		IGrid grid = modelFactory.createGrid();

		cluster1.setAdmin(admin);
		cluster1.setName("cluster1");
		cluster1.setGrid(grid);

		cluster2.setName("cluster2");
		cluster2.setAdmin(admin);
		cluster2.setGrid(grid);

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			em.persist(admin);
			em.persist(grid);
			em.persist(cluster1);
			em.persist(cluster2);
			tx.commit();
			
			clusterId = cluster1.getId();
		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}

}
