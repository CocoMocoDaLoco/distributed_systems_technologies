package dst.ass1.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
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
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.JobStatus;
import dst.ass1.jpa.model.ModelFactory;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a12 extends AbstractTest {

	private Long job1Id;
	private Long job2Id;
	private Long job3Id;
	private Long exec1Id;
	private Long exec2Id;
	private Long exec3Id;

	@Test
	public void testJobExecutionAssociation() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IJob> jobs = daoFactory.getJobDAO().findAll();

		assertNotNull(jobs);
		assertEquals(3, jobs.size());

		IJob j1 = jobs.get(0);
		IJob j2 = jobs.get(1);
		IJob j3 = jobs.get(2);

		assertEquals(job1Id, j1.getId());
		assertEquals(exec1Id, j1.getExecution().getId());

		assertEquals(job2Id, j2.getId());
		assertEquals(exec2Id, j2.getExecution().getId());

		assertEquals(job3Id, j3.getId());
		assertEquals(exec3Id, j3.getExecution().getId());

		List<IExecution> executions = daoFactory.getExecutionDAO().findAll();

		assertNotNull(executions);
		assertEquals(3, executions.size());

		IExecution e1 = executions.get(0);
		IExecution e2 = executions.get(1);
		IExecution e3 = executions.get(2);

		assertEquals(exec1Id, e1.getId());
		assertEquals(JobStatus.SCHEDULED, e1.getStatus());
		assertEquals(job1Id, e1.getJob().getId());

		assertEquals(exec2Id, e2.getId());
		assertEquals(JobStatus.SCHEDULED, e2.getStatus());
		assertEquals(job2Id, e2.getJob().getId());

		assertEquals(exec3Id, e3.getId());
		assertEquals(JobStatus.SCHEDULED, e3.getStatus());
		assertEquals(job3Id, e3.getJob().getId());
	}

	@Test
	public void testJobOptionalConstraint() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		IJob job = daoFactory.getJobDAO().findById(new Long(1));
		assertNotNull(job);

		boolean isConstraint = false;
		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();
			job.setExecution(null);
			em.persist(job);
			tx.commit();
		} catch (Exception e) {
			if (e.getCause().getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				isConstraint = true;
			}
		}

		assertTrue(isConstraint);
	}

	@Test
	public void testJobExecutionAssociationJdbc()
			throws ClassNotFoundException, SQLException {
		assertTrue(JdbcHelper.isColumnInTable("Job", "execution_id",
				jdbcConnection));
	}

	public void setUpDatabase() {
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			ModelFactory modelFactory = new ModelFactory();

			IUser user1 = modelFactory.createUser();
			user1.setUsername("user1");

			IUser user2 = modelFactory.createUser();
			user2.setUsername("user2");

			em.persist(user1);
			em.persist(user2);

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

			IJob job1 = modelFactory.createJob();
			job1.setEnvironment(environment1);
			job1.setExecution(ex1);
			job1.setUser(user1);
			user1.addJob(job1);

			IJob job2 = modelFactory.createJob();
			job2.setEnvironment(environment2);
			job2.setExecution(ex2);
			job2.setUser(user2);
			user2.addJob(job2);

			IJob job3 = modelFactory.createJob();
			job3.setEnvironment(environment3);
			job3.setExecution(ex3);
			job3.setUser(user1);
			user1.addJob(job3);

			ex1.setJob(job1);
			ex2.setJob(job2);
			ex3.setJob(job3);

			em.persist(environment1);
			em.persist(environment2);
			em.persist(environment3);
			em.persist(ex1);
			em.persist(ex2);
			em.persist(ex3);
			em.persist(job1);
			em.persist(job2);
			em.persist(job3);

			tx.commit();
			
			job1Id = job1.getId();
			job2Id = job2.getId();
			job3Id=job3.getId();
			exec1Id =  ex1.getId();
			exec2Id = ex2.getId();
			exec3Id = ex3.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
