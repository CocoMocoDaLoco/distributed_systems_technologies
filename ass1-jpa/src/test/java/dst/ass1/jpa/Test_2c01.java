package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.dao.IJobDAO;
import dst.ass1.jpa.model.IJob;

public class Test_2c01 extends AbstractTest {

	private TestData testData;

	@Before
	public void setUp() throws NoSuchAlgorithmException {
		testData = new TestData(em);
		testData.insertTestData();
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow1() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("user1",
				"workflow1");
		assertNotNull(jobs);
		assertEquals(1, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow2() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("user1",
				"workflow3");
		assertNotNull(jobs);
		assertEquals(0, jobs.size());
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow3() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("user2",
				"workflow1");
		assertNotNull(jobs);
		assertEquals(0, jobs.size());
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow4() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("user2", "");
		assertNotNull(jobs);
		assertEquals(1, jobs.size());
		assertEquals(testData.job2Id, jobs.get(0).getId());
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow5() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("user5",
				"workflow1");
		assertNotNull(jobs);
		assertEquals(0, jobs.size());
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow6() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("", "workflow1");
		assertNotNull(jobs);
		assertEquals(1, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow7() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			IJob job = daoFactory.getJobDAO().findById(new Long(3));
			job.getEnvironment().setWorkflow("workflow1");

			em.persist(job);

			tx.commit();

		} catch (Exception e) {
			fail(e.getMessage());
		}

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("user1",
				"workflow1");
		assertNotNull(jobs);
		assertEquals(2, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());
		assertEquals(testData.job3Id, jobs.get(1).getId());
	}

	@Test
	public void findAllJobsCreatedByUserAndExecuteWorkflow8() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		IJobDAO jobDao = daoFactory.getJobDAO();
		List<IJob> jobs = jobDao.findJobsForUserAndWorkflow("", "");
		assertNotNull(jobs);
		assertEquals(3, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());
		assertEquals(testData.job2Id, jobs.get(1).getId());
		assertEquals(testData.job3Id, jobs.get(2).getId());
	}
}
