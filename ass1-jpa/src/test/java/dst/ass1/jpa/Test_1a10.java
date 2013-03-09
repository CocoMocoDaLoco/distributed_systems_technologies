package dst.ass1.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IEnvironment;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a10 extends AbstractTest {

	private Long job1Id;
	private Long env1Id;
	
	@Test
	public void testJobEnvironmentAssociation() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);
		IJob job = daoFactory.getJobDAO().findById(job1Id);
		assertNotNull(job);
		assertEquals(env1Id, job.getEnvironment().getId());
	}

	@Test
	public void testJobEnvironmentAssociationJdbc()
			throws ClassNotFoundException, SQLException {
		assertTrue(JdbcHelper.isColumnInTable("Job", "environment_id",
				jdbcConnection));
		assertFalse(JdbcHelper.isColumnInTable("Environment", "job_id",
				jdbcConnection));
		assertTrue(JdbcHelper.isIndex("Job", "environment_id", false,
				jdbcConnection));
	}

	public void setUpDatabase() {
		IEnvironment e1 = modelFactory.createEnvironment();
		IJob job1 = modelFactory.createJob();
		job1.setEnvironment(e1);

		IExecution exec = modelFactory.createExecution();
		job1.setExecution(exec);

		IUser u1 = modelFactory.createUser();
		u1.setUsername("user1");
		u1.addJob(job1);
		job1.setUser(u1);

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			em.persist(u1);
			em.persist(e1);
			em.persist(exec);
			em.persist(job1);
			tx.commit();

			job1Id = job1.getId();
			env1Id =  e1.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}

	}

}
