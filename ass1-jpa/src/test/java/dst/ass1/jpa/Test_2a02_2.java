package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IEnvironment;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.JobStatus;
import dst.ass1.jpa.model.ModelFactory;

public class Test_2a02_2 extends AbstractTest {

	private TestData testData;

	@Before
	public void setUp() throws NoSuchAlgorithmException {
		testData = new TestData(em);
		testData.insertTestData();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testFindMostActiveUser() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());
			ModelFactory modelFactory = new ModelFactory();

			IUser u2 = daoFactory.getUserDAO().findById(testData.user2Id);
			assertNotNull(u2);

			IEnvironment environment = modelFactory.createEnvironment();
			environment.setWorkflow("workflow_env");
			environment.addParam("param");
			em.persist(environment);

			IExecution ex1 = modelFactory.createExecution();
			ex1.setStart(new Date(System.currentTimeMillis() - 18000000));
			ex1.setEnd(new Date());
			ex1.setStatus(JobStatus.SCHEDULED);

			IComputer computer1 = daoFactory.getComputerDAO().findById(
					testData.computer1Id);
			assertNotNull(computer1);
			ex1.addComputer(computer1);
			computer1.addExecution(ex1);

			IJob job = modelFactory.createJob();
			job.setNumCPUs(2);
			job.setExecutionTime(0);
			job.setEnvironment(environment);
			job.setExecution(ex1);
			job.setUser(u2);
			u2.addJob(job);

			em.persist(ex1);
			em.persist(computer1);
			em.persist(job);
			em.persist(u2);

			tx.commit();

			Query query = em.createNamedQuery("findMostActiveUser");

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(2, result.size());

			List<Long> ids = getUserIds(result);

			assertTrue(ids.contains(testData.user1Id));
			assertTrue(ids.contains(testData.user2Id));

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
}
