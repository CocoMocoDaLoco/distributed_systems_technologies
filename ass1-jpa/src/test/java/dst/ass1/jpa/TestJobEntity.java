package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
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
import dst.ass1.jpa.model.IEnvironment;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.JobStatus;

public class TestJobEntity extends AbstractTest {

	private Long job1Id;
	private Long job2Id;
	private Long job3Id;
	private Long environment1Id;
	private Long environment2Id;
	private Long environment3Id;
	private Long execution1Id;
	private Long execution2Id;
	private Long execution3Id;
	private Long user1Id;
	private Long user2Id;

	@Test
	public void testJob() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IJob> jobs = daoFactory.getJobDAO().findAll();

		assertNotNull(jobs);
		assertEquals(3, jobs.size());

		IJob j1 = daoFactory.getJobDAO().findById(job1Id);
		IJob j2 = daoFactory.getJobDAO().findById(job2Id);
		IJob j3 = daoFactory.getJobDAO().findById(job3Id);

		assertEquals(job1Id, j1.getId());
		assertEquals(false, j1.isPaid());
		assertEquals(environment1Id, j1.getEnvironment().getId());
		assertEquals(execution1Id, j1.getExecution().getId());
		assertEquals(user1Id, ((IPerson) j1.getUser()).getId());
		assertEquals(new Integer(0), j1.getExecutionTime());

		assertEquals(job2Id, j2.getId());
		assertEquals(false, j2.isPaid());
		assertEquals(environment2Id, j2.getEnvironment().getId());
		assertEquals(execution2Id, j2.getExecution().getId());
		assertEquals(user2Id, ((IPerson) j2.getUser()).getId());
		assertEquals(new Integer(0), j2.getExecutionTime());

		assertEquals(job3Id, j3.getId());
		assertEquals(false, j3.isPaid());
		assertEquals(environment3Id, j3.getEnvironment().getId());
		assertEquals(execution3Id, j3.getExecution().getId());
		assertEquals(user1Id, ((IPerson) j3.getUser()).getId());
		assertEquals(new Integer(0), j3.getExecutionTime());

	}

	@Test
	public void testJobJdbc() throws ClassNotFoundException, SQLException {
		String sql = "SELECT id, isPaid, environment_id, execution_id, user_id from Job order by id asc";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {

			long id = rs.getLong("id");

			if (id == job1Id.longValue()) {
				assertEquals((long) job1Id, rs.getLong("id"));
				assertFalse(rs.getBoolean("isPaid"));
				assertEquals((long) environment1Id,
						rs.getLong("environment_id"));
				assertEquals((long) execution1Id, rs.getLong("execution_id"));
				assertEquals((long) user1Id, rs.getLong("user_id"));
			} else if (id == job2Id.longValue()) {
				assertEquals((long) job2Id, rs.getLong("id"));
				assertFalse(rs.getBoolean("isPaid"));
				assertEquals((long) environment2Id,
						rs.getLong("environment_id"));
				assertEquals((long) execution2Id, rs.getLong("execution_id"));
				assertEquals((long) user2Id, rs.getLong("user_id"));
			} else if (id == job3Id.longValue()) {
				assertEquals((long) job3Id, rs.getLong("id"));
				assertFalse(rs.getBoolean("isPaid"));
				assertEquals((long) environment3Id,
						rs.getLong("environment_id"));
				assertEquals((long) execution3Id, rs.getLong("execution_id"));
				assertEquals((long) user1Id, rs.getLong("user_id"));
			} else {
				fail("Unexpected Job found!");
			}

		}

		rs.close();
		stmt.close();
	}

	protected void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IEnvironment environment1 = modelFactory.createEnvironment();
			environment1.setWorkflow("workflow1");
			environment1.addParam("param1");
			environment1.addParam("param2");
			environment1.addParam("param3");

			IEnvironment environment2 = modelFactory.createEnvironment();
			environment2.setWorkflow("workflow2");
			environment2.addParam("param4");

			IEnvironment environment3 = modelFactory.createEnvironment();
			environment2.setWorkflow("workflow3");
			environment3.addParam("param5");

			em.persist(environment1);
			em.persist(environment2);
			em.persist(environment3);

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

			IUser user1 = modelFactory.createUser();
			IUser user2 = modelFactory.createUser();

			user1.setUsername("u1");
			user2.setUsername("u2");

			// Jobs
			IJob job1 = modelFactory.createJob();
			job1.setNumCPUs(2);
			job1.setExecutionTime(0);
			job1.setEnvironment(environment1);
			job1.setExecution(ex1);
			job1.setUser(user1);
			user1.addJob(job1);

			IJob job2 = modelFactory.createJob();
			job2.setNumCPUs(3);
			job2.setExecutionTime(0);
			job2.setEnvironment(environment2);
			job2.setExecution(ex2);
			job2.setUser(user2);
			user2.addJob(job2);

			IJob job3 = modelFactory.createJob();
			job3.setNumCPUs(4);
			job3.setExecutionTime(0);
			job3.setEnvironment(environment3);
			job3.setExecution(ex3);
			job3.setUser(user1);
			user1.addJob(job3);

			ex1.setJob(job1);
			ex2.setJob(job2);
			ex3.setJob(job3);

			em.persist(ex1);
			em.persist(ex2);
			em.persist(ex3);

			em.persist(user1);
			em.persist(user2);
			em.persist(job1);
			em.persist(job2);
			em.persist(job3);

			tx.commit();

			job1Id = job1.getId();
			job2Id = job2.getId();
			job3Id = job3.getId();
			environment1Id = environment1.getId();
			environment2Id = environment2.getId();
			environment3Id = environment3.getId();
			execution1Id = ex1.getId();
			execution2Id = ex2.getId();
			execution3Id = ex3.getId();
			user1Id = ((IPerson) user1).getId();
			user2Id = ((IPerson) user2).getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
