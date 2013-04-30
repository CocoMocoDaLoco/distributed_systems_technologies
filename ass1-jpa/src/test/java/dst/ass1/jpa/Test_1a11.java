package dst.ass1.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
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
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a11 extends AbstractTest {

	private Long user1Id;
	private Long user2Id;
	
	private Long job1Id;
	private Long job2Id;
	private Long job3Id;

	@Test
	public void testUserJobAssociation() {

		System.out.println();

		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		IUser user = daoFactory.getUserDAO().findById(user1Id);
		assertNotNull(user);
		assertNotNull(user.getJobs());

		List<Long> jobIds = getJobIds(user.getJobs());

		assertEquals(2, jobIds.size());

		assertTrue(jobIds.contains(job1Id));
		assertTrue(jobIds.contains(job3Id));

		IJob job1 = daoFactory.getJobDAO().findById(job1Id);
		IJob job2 = daoFactory.getJobDAO().findById(job2Id);

		assertNotNull(job1);
		assertNotNull(job2);

		assertEquals(user1Id, ((IPerson) job1.getUser()).getId());
		assertEquals(user2Id, ((IPerson) job2.getUser()).getId());
	}

	@Test
	public void testUserJobAssociationJdbc() throws ClassNotFoundException,
			SQLException {
		assertTrue(JdbcHelper.isColumnInTable("Job", "user_id", jdbcConnection));
	}

	public void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			IUser user1 = modelFactory.createUser();
			user1.setUsername("user1");

			IUser user2 = modelFactory.createUser();
			user2.setUsername("user2");

			em.persist(user1);
			em.persist(user2);

			IEnvironment environment1 = modelFactory.createEnvironment();
			IEnvironment environment2 = modelFactory.createEnvironment();
			IEnvironment environment3 = modelFactory.createEnvironment();

			em.persist(environment1);
			em.persist(environment2);
			em.persist(environment3);

			IExecution ex1 = modelFactory.createExecution();
			IExecution ex2 = modelFactory.createExecution();
			IExecution ex3 = modelFactory.createExecution();

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

			em.persist(user1);
			em.persist(user2);
			em.persist(job1);
			em.persist(job2);
			em.persist(job3);

			tx.commit();
			
			user1Id = ((IPerson) user1).getId();
			user2Id = ((IPerson) user2).getId();

			job1Id = job1.getId();
			job2Id = job2.getId();
			job3Id = job3.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}

	}
}
