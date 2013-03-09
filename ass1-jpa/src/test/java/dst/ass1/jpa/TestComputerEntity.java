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
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.JobStatus;

public class TestComputerEntity extends AbstractTest {

	private Long cluster1Id;
	private Long cluster2Id;
	private Long execution1Id;
	private Long execution2Id;
	private Long execution3Id;
	private Long computer1Id;
	private Long computer2Id;
	private Long computer3Id;

	@Test
	public void testComputer() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IComputer> computers = daoFactory.getComputerDAO().findAll();

		assertNotNull(computers);

		IComputer cl1_comp1 = computers.get(0);
		IComputer cl1_comp2 = computers.get(1);
		IComputer cl2_comp1 = computers.get(2);

		assertEquals("computer1", cl1_comp1.getName());
		assertEquals(new Integer(2), cl1_comp1.getCpus());
		assertNotNull(cl1_comp1.getCreation());
		assertNotNull(cl1_comp1.getLastUpdate());
		
		assertEquals("AUT-VIE-location1", cl1_comp1.getLocation());
		assertNotNull(cl1_comp1.getCluster());
		assertEquals(cluster1Id, cl1_comp1.getCluster().getId());
		assertEquals(1, cl1_comp1.getExecutions().size());
		assertEquals(execution1Id, cl1_comp1.getExecutions().get(0).getId());

		assertEquals("computer2", cl1_comp2.getName());
		assertEquals(new Integer(3), cl1_comp2.getCpus());
		assertNotNull(cl1_comp2.getCreation());
		assertNotNull(cl1_comp2.getLastUpdate());
		
		assertEquals("AUT-VIE-location2", cl1_comp2.getLocation());
		assertNotNull(cl1_comp2.getCluster());
		assertEquals(cluster1Id, cl1_comp2.getCluster().getId());
		assertEquals(1, cl1_comp2.getExecutions().size());
		assertEquals(execution2Id, cl1_comp2.getExecutions().get(0).getId());

		assertEquals("computer3", cl2_comp1.getName());
		assertEquals(new Integer(4), cl2_comp1.getCpus());
		assertNotNull(cl2_comp1.getCreation());
		assertNotNull(cl2_comp1.getLastUpdate());
		
		assertEquals("AUT-VIE-location3", cl2_comp1.getLocation());
		assertNotNull(cl2_comp1.getCluster());
		assertEquals(cluster2Id, cl2_comp1.getCluster().getId());
		assertEquals(1, cl2_comp1.getExecutions().size());
		assertEquals(execution3Id, cl2_comp1.getExecutions().get(0).getId());

	}

	@Test
	public void testComputerJdbc() throws ClassNotFoundException, SQLException {
		String sql = "select id, cpus, creation, lastUpdate, location, name, cluster_id from Computer";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		assertTrue(rs.next());

		assertEquals((long) computer1Id, rs.getLong("id"));
		assertEquals(2, rs.getLong("cpus"));
		assertNotNull(rs.getDate("creation"));
		assertNotNull(rs.getDate("lastUpdate"));
		assertEquals("AUT-VIE-location1", rs.getString("location"));
		assertEquals("computer1", rs.getString("name"));
		assertEquals((long) cluster1Id, rs.getLong("cluster_id"));

		assertTrue(rs.next());

		assertEquals((long) computer2Id, rs.getLong("id"));
		assertEquals(3, rs.getLong("cpus"));
		assertNotNull(rs.getDate("creation"));
		assertNotNull(rs.getDate("lastUpdate"));
		assertEquals("AUT-VIE-location2", rs.getString("location"));
		assertEquals("computer2", rs.getString("name"));
		assertEquals((long) cluster1Id, rs.getLong("cluster_id"));

		assertTrue(rs.next());

		assertEquals((long) computer3Id, rs.getLong("id"));
		assertEquals(4, rs.getLong("cpus"));
		assertNotNull(rs.getDate("creation"));
		assertNotNull(rs.getDate("lastUpdate"));
		assertEquals("AUT-VIE-location3", rs.getString("location"));
		assertEquals("computer3", rs.getString("name"));
		assertEquals((long) cluster2Id, rs.getLong("cluster_id"));

		assertFalse(rs.next());

		rs.close();
	}

	@Test
	public void testExecutionComputerJdbc() throws ClassNotFoundException,
			SQLException {
		String sql = "select executions_id, computers_id from execution_computer";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		assertTrue(rs.next());

		assertEquals((long) execution1Id, rs.getLong("executions_id"));
		assertEquals((long) computer1Id, rs.getLong("computers_id"));

		assertTrue(rs.next());

		assertEquals((long) execution2Id, rs.getLong("executions_id"));
		assertEquals((long) computer2Id, rs.getLong("computers_id"));

		assertTrue(rs.next());

		assertEquals((long) execution3Id, rs.getLong("executions_id"));
		assertEquals((long) computer3Id, rs.getLong("computers_id"));

		assertFalse(rs.next());

		rs.close();
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

			IExecution ex1 = modelFactory.createExecution();
			ex1.setStart(new Date(System.currentTimeMillis() - 36000000));
			ex1.setEnd(new Date());
			ex1.setStatus(JobStatus.SCHEDULED);

			IExecution ex2 = modelFactory.createExecution();
			ex2.setStart(new Date());
			ex2.setStatus(JobStatus.SCHEDULED);

			IExecution ex3 = modelFactory.createExecution();
			ex3.setStart(new Date());
			ex3.setStatus(JobStatus.SCHEDULED);

			ex1.addComputer(computer1);
			ex2.addComputer(computer2);
			ex3.addComputer(computer3);

			computer1.addExecution(ex1);
			computer2.addExecution(ex2);
			computer3.addExecution(ex3);

			em.persist(grid);
			em.persist(admin1);
			em.persist(admin2);
			em.persist(cluster1);
			em.persist(cluster2);
			em.persist(ex1);
			em.persist(ex2);
			em.persist(ex3);
			em.persist(computer1);
			em.persist(computer2);
			em.persist(computer3);

			tx.commit();

			computer1Id = computer1.getId();
			computer2Id = computer2.getId();
			computer3Id = computer3.getId();
			cluster1Id = cluster1.getId();
			cluster2Id = cluster2.getId();
			execution1Id = ex1.getId();
			execution2Id = ex2.getId();
			execution3Id = ex3.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
