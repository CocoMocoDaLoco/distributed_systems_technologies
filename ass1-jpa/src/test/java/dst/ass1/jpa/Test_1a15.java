package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IGrid;

public class Test_1a15 extends AbstractTest {

	private Long cluster1Id;
	private Long cluster2Id;

	@Test
	public void testClusterAssociation() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		ICluster cl1 = daoFactory.getClusterDAO().findById(cluster1Id);
		assertNotNull(cl1.getComposedOf());
		assertEquals(1, cl1.getComposedOf().size());
		assertEquals(cluster2Id, cl1.getComposedOf().get(0).getId());
	}

	@Test
	public void testClusterComposedJdbc() throws ClassNotFoundException,
			SQLException {
		String sql = "SELECT partOf_id, composedOf_id from composed_of";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());

		assertEquals((long) cluster1Id, rs.getLong("partOf_id"));
		assertEquals((long) cluster2Id, rs.getLong("composedOf_id"));

		assertFalse(rs.next());

		rs.close();
	}

	public void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {

			tx.begin();

			IAdmin admin = modelFactory.createAdmin();
			IGrid grid = modelFactory.createGrid();

			em.persist(admin);
			em.persist(grid);

			ICluster cluster1 = modelFactory.createCluster();
			cluster1.setAdmin(admin);
			cluster1.setGrid(grid);
			cluster1.setName("cluster1");
			cluster1.setLastService(new Date());
			cluster1.setNextService(new Date());

			ICluster cluster2 = modelFactory.createCluster();
			cluster2.setAdmin(admin);
			cluster2.setGrid(grid);
			cluster2.setName("cluster2");
			cluster2.setLastService(new Date());
			cluster2.setNextService(new Date());

			cluster1.addComposedOf(cluster2);
			cluster2.addPartOf(cluster1);

			em.persist(cluster1);
			em.persist(cluster2);

			tx.commit();

			cluster1Id = cluster1.getId();
			cluster2Id = cluster2.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
