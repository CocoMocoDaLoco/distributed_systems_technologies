package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.JobStatus;

public class Test_2c02 extends AbstractTest {

	private TestData testData;

	@Before
	public void setUp() throws NoSuchAlgorithmException {
		testData = new TestData(em);
		testData.insertTestData();
	}

	@Test
	public void testFindJobsForStatusFinishedStartandFinish1() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		List<IJob> jobs = daoFactory.getJobDAO()
				.findJobForStatusFinishedStartandFinish(null, null);
		boolean isNoJobs = jobs == null || jobs.size() == 0;
		assertTrue(isNoJobs);
	}

	@Test
	public void testFindJobsForStatusFinishedStartandFinish2() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			IJob job = daoFactory.getJobDAO().findById(testData.job1Id);
			job.getExecution().setStatus(JobStatus.FINISHED);

			em.persist(job);

			tx.commit();

		} catch (Exception e) {
			fail(e.getMessage());
		}

		List<IJob> jobs = daoFactory.getJobDAO()
				.findJobForStatusFinishedStartandFinish(null, null);
		assertNotNull(jobs);
		assertEquals(1, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());
	}

	@Test
	public void testFindJobsForStatusFinishedStartandFinish3() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		try {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			IJob job = daoFactory.getJobDAO().findById(testData.job1Id);
			job.getExecution().setStatus(JobStatus.FINISHED);
			job.getExecution().setStart(createDate(2012, 1, 20));
			job.getExecution().setEnd(createDate(2012, 11, 30));

			em.persist(job);

			tx.commit();

		} catch (Exception e) {
			fail(e.getMessage());
		}

		List<IJob> jobs = daoFactory.getJobDAO()
				.findJobForStatusFinishedStartandFinish(createDate(2012, 1, 1),
						createDate(2012, 12, 31));
		assertNotNull(jobs);
		assertEquals(0, jobs.size());

		jobs = daoFactory.getJobDAO().findJobForStatusFinishedStartandFinish(
				createDate(2012, 1, 1), createDate(2012, 10, 1));
		assertNotNull(jobs);
		assertEquals(0, jobs.size());

		jobs = daoFactory.getJobDAO().findJobForStatusFinishedStartandFinish(
				createDate(2012, 10, 1), createDate(2012, 12, 1));
		assertNotNull(jobs);
		assertEquals(0, jobs.size());

		jobs = daoFactory.getJobDAO().findJobForStatusFinishedStartandFinish(
				createDate(2012, 1, 20), createDate(2012, 11, 30));
		assertEquals(1, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());

		jobs = daoFactory.getJobDAO().findJobForStatusFinishedStartandFinish(
				createDate(2012, 1, 20), null);
		assertEquals(1, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());

		jobs = daoFactory.getJobDAO().findJobForStatusFinishedStartandFinish(
				null, createDate(2012, 11, 30));
		assertEquals(1, jobs.size());
		assertEquals(testData.job1Id, jobs.get(0).getId());

	}

	private Date createDate(int year, int month, int day) {

		String temp = year + "/" + month + "/" + day;
		Date date = null;

		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
			date = formatter.parse(temp);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;

	}

}
