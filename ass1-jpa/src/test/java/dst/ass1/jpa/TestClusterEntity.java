package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.dao.IClusterDAO;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IPerson;

public class TestClusterEntity extends AbstractTest {

	private Long gridId;
	private Long cluster1Id;
	private Long cluster2Id;
	private Long computer1Id;
	private Long computer2Id;
	private Long computer3Id;
	private Long admin1Id;
	private Long admin2Id;

	@Test
	public void testCluster() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		IClusterDAO clusterDao = daoFactory.getClusterDAO();
		List<ICluster> clusters = clusterDao.findAll();

		assertNotNull(clusters);
		assertEquals(2, clusters.size());

		ICluster cl1 = daoFactory.getClusterDAO().findById(cluster1Id);
		ICluster cl2 = daoFactory.getClusterDAO().findById(cluster2Id);

		assertEquals(cluster1Id, cl1.getId());
		assertEquals("cluster1", cl1.getName());
		assertNotNull(cl1.getLastService());
		assertNotNull(cl1.getNextService());

		assertEquals(cluster2Id, cl2.getId());
		assertEquals("cluster2", cl2.getName());
		assertNotNull(cl2.getLastService());
		assertNotNull(cl2.getNextService());

		assertNotNull(cl1.getComputers());
		assertEquals(2, cl1.getComputers().size());

		List<Long> cl1_computerIds = getComputerIds(cl1.getComputers());
		assertTrue(cl1_computerIds.contains(computer1Id));
		assertTrue(cl1_computerIds.contains(computer2Id));

		assertNotNull(cl2.getComputers());
		assertEquals(1, cl2.getComputers().size());

		List<Long> cl2_computerIds = getComputerIds(cl2.getComputers());
		assertTrue(cl2_computerIds.contains(computer3Id));

		assertNotNull(cl1.getAdmin());
		assertEquals(admin1Id, ((IPerson) cl1.getAdmin()).getId());

		assertNotNull(cl2.getAdmin());
		assertEquals(admin2Id, ((IPerson) cl2.getAdmin()).getId());

		assertNotNull(cl1.getGrid());
		assertEquals(gridId, cl1.getGrid().getId());

		assertNotNull(cl2.getGrid());
		assertEquals(gridId, cl2.getGrid().getId());

		assertNotNull(cl1.getLastService());
		assertNotNull(cl2.getLastService());
	}

	@Test
	public void testClusterJdbc() throws ClassNotFoundException, SQLException {
		String sql = "SELECT id, lastService, name, nextService, admin_id, grid_id FROM Cluster order by id asc";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {

			long id = rs.getLong("id");

			if (id == cluster1Id.longValue()) {
				assertNotNull(rs.getDate("lastService"));
				assertEquals("cluster1", rs.getString("name"));
				assertNotNull(rs.getDate("nextService"));
				assertEquals((long) admin1Id, rs.getLong("admin_id"));
				assertEquals((long) gridId, rs.getLong("grid_id"));
			} else if (id == cluster2Id.longValue()) {
				assertNotNull(rs.getDate("lastService"));
				assertEquals("cluster2", rs.getString("name"));
				assertNotNull(rs.getDate("nextService"));
				assertEquals((long) admin2Id, rs.getLong("admin_id"));
				assertEquals((long) gridId, rs.getLong("grid_id"));
			} else {
				fail("Unexpected Cluster found!");
			}
		}

		rs.close();
		stmt.close();
	}

	protected void setUpDatabase() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IAdmin admin1 = modelFactory.createAdmin();
			IAdmin admin2 = modelFactory.createAdmin();

			// Computers
			IComputer computer1 = modelFactory.createComputer();
			computer1.setName("computer1");
			computer1.setCpus(2);
			computer1.setLocation("AUT-VIE-location1");
			computer1.setCreation(new Date(0));
			computer1.setLastUpdate(new Date(0));

			IComputer computer2 = modelFactory.createComputer();
			computer2.setName("computer2");
			computer2.setCpus(3);
			computer2.setLocation("AUT-VIE-location2");
			computer2.setCreation(new Date(0));
			computer2.setLastUpdate(new Date(0));

			IComputer computer3 = modelFactory.createComputer();
			computer3.setName("computer3");
			computer3.setCpus(4);
			computer3.setLocation("AUT-VIE-location3");
			computer3.setCreation(new Date(0));
			computer3.setLastUpdate(new Date(0));

			// Clusters
			ICluster cluster1 = modelFactory.createCluster();
			cluster1.setAdmin(admin1);
			cluster1.setName("cluster1");
			cluster1.setLastService(new Date());
			cluster1.setNextService(new Date());

			ICluster cluster2 = modelFactory.createCluster();
			cluster2.setAdmin(admin2);
			cluster2.setName("cluster2");
			cluster2.setLastService(new Date());
			cluster2.setNextService(new Date());

			cluster1.addComposedOf(cluster2);
			cluster2.addPartOf(cluster1);

			admin1.addCluster(cluster1);
			admin2.addCluster(cluster2);

			cluster1.addComputer(computer1);
			cluster1.addComputer(computer2);
			computer1.setCluster(cluster1);
			computer2.setCluster(cluster1);

			cluster2.addComputer(computer3);
			computer3.setCluster(cluster2);

			IGrid grid = modelFactory.createGrid();
			cluster1.setGrid(grid);
			cluster2.setGrid(grid);

			em.persist(grid);
			em.persist(admin1);
			em.persist(admin2);
			em.persist(cluster1);
			em.persist(cluster2);
			em.persist(computer1);
			em.persist(computer2);
			em.persist(computer3);

			tx.commit();

			gridId = grid.getId();
			admin1Id = ((IPerson) admin1).getId();
			admin2Id = ((IPerson) admin2).getId();
			cluster1Id = cluster1.getId();
			cluster2Id = cluster2.getId();
			computer1Id = computer1.getId();
			computer2Id = computer2.getId();
			computer3Id = computer3.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());

		}

	}

}
