package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
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

public class TestExecutionEntity extends AbstractTest {

	private Long execution1Id;
	private Long execution2Id;
	private Long execution3Id;
	private Long computer1Id;
	private Long computer2Id;
	private Long computer3Id;

	@Test
	public void testExecution() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IExecution> executions = daoFactory.getExecutionDAO().findAll();

		assertNotNull(executions);
		assertEquals(3, executions.size());

		IExecution e1 = executions.get(0);
		IExecution e2 = executions.get(1);
		IExecution e3 = executions.get(2);

		assertEquals(execution1Id, e1.getId());
		assertNotNull(e1.getStart());
		assertNotNull(e1.getEnd());
		assertEquals(JobStatus.SCHEDULED, e1.getStatus());
		assertNotNull(e1.getComputers());
		assertEquals(1, e1.getComputers().size());
		assertEquals(computer1Id, e1.getComputers().get(0).getId());

		assertEquals(execution2Id, e2.getId());
		assertNotNull(e2.getStart());
		assertNull(e2.getEnd());
		assertEquals(JobStatus.SCHEDULED, e2.getStatus());
		assertNotNull(e2.getComputers());
		assertEquals(1, e2.getComputers().size());
		assertEquals(computer2Id, e2.getComputers().get(0).getId());

		assertEquals(execution3Id, e3.getId());
		assertNotNull(e3.getStart());
		assertNull(e3.getEnd());
		assertEquals(JobStatus.SCHEDULED, e3.getStatus());
		assertNotNull(e3.getComputers());
		assertEquals(1, e3.getComputers().size());
		assertEquals(computer3Id, e3.getComputers().get(0).getId());

	}

	@Test
	public void testExecutionJdbc() throws SQLException, ClassNotFoundException {
		String sql = "select id, end, start, status from Execution order by id asc";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		assertTrue(rs.next());

		assertEquals((long) execution1Id, rs.getLong("id"));
		assertNotNull(rs.getDate("end"));
		assertNotNull(rs.getDate("start"));
		assertEquals("SCHEDULED", rs.getString("status"));

		assertTrue(rs.next());

		assertEquals((long) execution2Id, rs.getLong("id"));
		assertNull(rs.getDate("end"));
		assertNotNull(rs.getDate("start"));
		assertEquals("SCHEDULED", rs.getString("status"));

		assertTrue(rs.next());

		assertEquals((long) execution3Id, rs.getLong("id"));
		assertNull(rs.getDate("end"));
		assertNotNull(rs.getDate("start"));
		assertEquals("SCHEDULED", rs.getString("status"));

		assertFalse(rs.next());

		rs.close();
	}

	protected void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			
			IAdmin admin1 = modelFactory.createAdmin();

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

			ICluster cluster1 = modelFactory.createCluster();
			cluster1.setAdmin(admin1);
			cluster1.setName("cluster1");
			cluster1.setLastService(new Date());
			cluster1.setNextService(new Date());
			admin1.addCluster(cluster1);

			cluster1.addComputer(computer1);
			cluster1.addComputer(computer2);
			cluster1.addComputer(computer3);
			computer1.setCluster(cluster1);
			computer2.setCluster(cluster1);
			computer3.setCluster(cluster1);

			IGrid grid = modelFactory.createGrid();
			cluster1.setGrid(grid);

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
			em.persist(cluster1);
			em.persist(ex1);
			em.persist(ex2);
			em.persist(ex3);
			em.persist(computer1);
			em.persist(computer2);
			em.persist(computer3);

			tx.commit();

			execution1Id = ex1.getId();
			execution2Id = ex2.getId();
			execution3Id = ex3.getId();
			computer1Id = computer1.getId();
			computer2Id = computer2.getId();
			computer3Id = computer3.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}