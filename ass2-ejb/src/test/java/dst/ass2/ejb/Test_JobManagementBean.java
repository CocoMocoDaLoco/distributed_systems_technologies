package dst.ass2.ejb;

import static dst.ass2.ejb.util.EJBUtils.lookup;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.NoSuchEJBException;
import javax.naming.NamingException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dst.ass2.AbstractEJBTest;
import dst.ass2.ejb.dto.AssignmentDTO;
import dst.ass2.ejb.session.JobManagementBean;
import dst.ass2.ejb.session.exception.AssignmentException;
import dst.ass2.ejb.session.interfaces.IJobManagementBean;
import dst.ass2.util.JobHelperDTO;

public class Test_JobManagementBean extends AbstractEJBTest {

	private IJobManagementBean jobManagementBean;

	@Before
	public void setUp() throws NamingException {
		managementBean.clearPriceCache();
		jobManagementBean = lookup(ctx, JobManagementBean.class);
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testCache() {
		// cache should be empty
		List<AssignmentDTO> cache = jobManagementBean.getCache();
		assertNotNull(cache);
		assertEquals(0, cache.size());
	}

	@Test
	public void testLogin_With_CorrectCredentials() {
		try {
			jobManagementBean.login("hansi", "pw");
		} catch (AssignmentException e) {
			e.printStackTrace();
			fail(String.format("Unexpected Exception: %s !", e.getMessage()));
		}
	}

	@Test
	public void testLogin_With_InvalidCredentials() {
		try {
			jobManagementBean.login("hansi", "pw1");
			fail("Login with invalid credentials passed. Expected: "
					+ AssignmentException.class.getName());
		} catch (AssignmentException e) {
			// expected exception
		}
	}

	@Test
	public void testAddJob_And_removeJobsForGrid() {
		try {

			// remove all jobs from db, since one job is included in the
			// test-data
			jdbcTestUtil.removeAllPrices_FROM_DB();

			// get the grids from the database
			List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();
			assertEquals(2, gridIds.size());

			// add 2 jobs for same grid
			jobManagementBean.addJob(gridIds.get(0), 1, "workflow",
					new ArrayList<String>());
			jobManagementBean.addJob(gridIds.get(0), 2, "workflow1",
					new ArrayList<String>());

			// there should be two jobs in the cache
			List<AssignmentDTO> cache = jobManagementBean.getCache();
			assertNotNull(cache);
			assertEquals(2, cache.size());
			assertTrue(isJobInCache(new AssignmentDTO(gridIds.get(0), 1,
					"workflow", new ArrayList<String>(), null), cache));
			assertTrue(isJobInCache(new AssignmentDTO(gridIds.get(0), 2,
					"workflow1", new ArrayList<String>(), null), cache));

			// remove jobs for wrong grid
			jobManagementBean.removeJobsForGrid(Long.MAX_VALUE);

			// cache should stay the same
			cache = jobManagementBean.getCache();
			assertNotNull(cache);
			assertEquals(2, cache.size());
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(0), Integer.valueOf(1),
							"workflow", new ArrayList<String>(),
							new ArrayList<Long>()), cache));
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(0), Integer.valueOf(2),
							"workflow1", new ArrayList<String>(),
							new ArrayList<Long>()), cache));

			// remove jobs for grid
			jobManagementBean.removeJobsForGrid(gridIds.get(0));

			// cache should be empty
			cache = jobManagementBean.getCache();
			assertNotNull(cache);
			assertEquals(0, cache.size());

		} catch (AssignmentException e) {
			e.printStackTrace();
			fail(String.format("Unexpected Exception: %s !", e.getMessage()));
		}

	}

	@Test
	public void testAddJob_With_Login_And_Check_Discarded() {
		try {
			// remove all jobs from db, since on job is included in the
			// test-data
			jdbcTestUtil.removeAllJobs_FROM_DB();

			// get the grids from the database
			List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();
			assertEquals(2, gridIds.size());

			managementBean_addPrices();

			jobManagementBean.login("hansi", "pw");

			List<String> params1 = new ArrayList<String>();
			params1.add("param1");
			params1.add("param2");
			jobManagementBean.addJob(gridIds.get(0), 2, "workflow1", params1);

			List<String> params2 = new ArrayList<String>();
			params2.add("param3");
			jobManagementBean.addJob(gridIds.get(1), 6, "workflow2", params2);

			// check cache
			List<AssignmentDTO> cache = jobManagementBean.getCache();
			assertNotNull(cache);
			assertEquals(2, cache.size());
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(0), Integer.valueOf(2),
							"workflow1", params1, new ArrayList<Long>()), cache));
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(1), Integer.valueOf(6),
							"workflow2", params2, new ArrayList<Long>()), cache));

			jobManagementBean.submitAssignments();

			// there have to be 2 jobs in total in the db
			List<JobHelperDTO> jobs = jdbcTestUtil.getAllJobs_FROM_DB();

			assertNotNull(jobs);
			assertEquals(2, jobs.size());

			assertTrue(checkJobInList("workflow1", "hansi", params1, jobs));
			assertTrue(checkJobInList("workflow2", "hansi", params2, jobs));

			// check if the bean was discarded after submitAssignments() was
			// called successfully!
			try {
				jobManagementBean.getCache();
				fail(NoSuchEJBException.class.getName() + " expected!");
			} catch (NoSuchEJBException e) {
				// Expected
			}

		} catch (AssignmentException e) {
			e.printStackTrace();
			fail(String.format("Unexpected Exception: %s !", e.getMessage()));
		}

	}

	@Test
	public void testAddJob_With_Login_AnotherUser() {
		try {
			// remove all jobs from db, since on job is included in the
			// test-data
			jdbcTestUtil.removeAllJobs_FROM_DB();

			// get the grids from the database
			List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();
			assertEquals(2, gridIds.size());

			managementBean_addPrices();

			jobManagementBean.login("franz", "liebe");

			List<String> params1 = new ArrayList<String>();
			params1.add("param1");
			params1.add("param2");
			params1.add("param3");
			params1.add("param4");

			jobManagementBean.addJob(gridIds.get(0), 2, "workflow3", params1);

			// check cache
			List<AssignmentDTO> cache = jobManagementBean.getCache();
			assertNotNull(cache);
			assertEquals(1, cache.size());
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(0), Integer.valueOf(2),
							"workflow3", params1, new ArrayList<Long>()), cache));

			jobManagementBean.submitAssignments();

			// there has to be 1 job in total in the db
			List<JobHelperDTO> jobs = jdbcTestUtil.getAllJobs_FROM_DB();

			assertNotNull(jobs);
			assertEquals(1, jobs.size());

			assertTrue(checkJobInList("workflow3", "franz", params1, jobs));

		} catch (AssignmentException e) {
			e.printStackTrace();
			fail(String.format("Unexpected Exception: %s !", e.getMessage()));
		}
	}

	@Test
	public void testAddJob_Without_Login() {
		try {
			// remove all jobs from db, since on job is included in the
			// test-data
			jdbcTestUtil.removeAllJobs_FROM_DB();

			// get the grids from the database
			List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();
			assertEquals(2, gridIds.size());

			managementBean.addPrice(0, new BigDecimal(50));

			List<String> params1 = new ArrayList<String>();
			params1.add("param1");
			params1.add("param2");
			jobManagementBean.addJob(gridIds.get(0), 2, "workflow1", params1);

			// try to submit assignments => without log in
			jobManagementBean.submitAssignments();

			fail("Assignments got submitted without login. AssignmentException expected!");
		} catch (AssignmentException e) {
			// Expected Exception
		}

		// there shouldn't be any jobs in the db
		assertEquals(0, jdbcTestUtil.getNumberOfJobs_FROM_DB());

	}

	@Test
	public void testAddJob_Without_Login2() {
		try {
			// remove all jobs from db, since on job is included in the
			// test-data
			jdbcTestUtil.removeAllJobs_FROM_DB();

			// get the grids from the database
			List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();
			assertEquals(2, gridIds.size());

			managementBean_addPrices();

			List<String> params1 = new ArrayList<String>();
			params1.add("param1");
			params1.add("param2");
			jobManagementBean.addJob(gridIds.get(0), 2, "workflow1", params1);

			List<String> params2 = new ArrayList<String>();
			params2.add("param1");
			jobManagementBean.addJob(gridIds.get(1), 6, "workflow2", params2);

			// check cache
			List<AssignmentDTO> cache = jobManagementBean.getCache();
			assertNotNull(cache);
			assertEquals(2, cache.size());
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(0), Integer.valueOf(2),
							"workflow1", params1, new ArrayList<Long>()), cache));
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(1), Integer.valueOf(6),
							"workflow2", params2, new ArrayList<Long>()), cache));

			// try to submit assignments => without log in
			try {
				jobManagementBean.submitAssignments();
				fail("Assignments got submitted without login. AssignmentException expected!");
			} catch (AssignmentException e) {
				// Expected Exception
			}

			// check cache => should not be cleared
			cache = jobManagementBean.getCache();
			assertNotNull(cache);
			assertEquals(2, cache.size());
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(0), Integer.valueOf(2),
							"workflow1", params1, new ArrayList<Long>()), cache));
			assertTrue(isJobInCache(
					new AssignmentDTO(gridIds.get(1), Integer.valueOf(6),
							"workflow2", params2, new ArrayList<Long>()), cache));

			// there shouldn't be any jobs in the db
			assertEquals(0, jdbcTestUtil.getNumberOfJobs_FROM_DB());

			// login
			jobManagementBean.login("hansi", "pw");
			// successfully submit Assignments
			jobManagementBean.submitAssignments();

			// check the database here

			// there have to be 2 jobs in total in the db
			List<JobHelperDTO> jobs = jdbcTestUtil.getAllJobs_FROM_DB();

			assertNotNull(jobs);
			assertEquals(2, jobs.size());

			assertTrue(checkJobInList("workflow1", "hansi", params1, jobs));
			assertTrue(checkJobInList("workflow2", "hansi", params2, jobs));

		} catch (AssignmentException e) {
			e.printStackTrace();
			fail(String.format("Unexpected Exception: %s !", e.getMessage()));
		}

	}

	@Test
	public void testAddJob_With_WrongGridId() {
		// remove all jobs from db, since on job is included in the
		// test-data
		jdbcTestUtil.removeAllJobs_FROM_DB();

		long nonExistingGrid = Long.MAX_VALUE;

		// try to add a job for a grid that does not exist in the db.
		try {
			jobManagementBean.addJob(nonExistingGrid, 3, "workflow5",
					new ArrayList<String>());
			fail("Grid ID is not available in DB. AssignmentException expected!");
		} catch (AssignmentException e) {
			// expected Exception
		}

		// cache should be empty
		List<AssignmentDTO> cache = jobManagementBean.getCache();
		assertNotNull(cache);
		assertEquals(0, cache.size());

		// also db should be empty
		assertEquals(0, jdbcTestUtil.getNumberOfJobs_FROM_DB());
	}

	private boolean checkJobInList(String workflow, String username,
			List<String> parameters, List<JobHelperDTO> jobs) {

		for (JobHelperDTO job : jobs) {
			String jobWorkflow = job.getWorkflow();
			assertNotNull(job.getWorkflow());
			String jobUsername = job.getUsername();
			assertNotNull(jobUsername);
			List<String> jobParams = job.getParams();
			assertNotNull(jobParams);
			assertNotNull(job.getStart());

			if (jobWorkflow.equals(workflow) && jobUsername.equals(username)
					&& jobParams.equals(parameters))
				return true;
		}

		return false;
	}

	private boolean isJobInCache(AssignmentDTO assignmentDTO,
			List<AssignmentDTO> cache) {
		for (AssignmentDTO cached : cache) {
			Long gridId = cached.getGridId();
			Integer numberOfCpus = cached.getNumCPUs();
			List<String> params = cached.getParams();
			String workflow = cached.getWorkflow();
			List<Long> computerIds = cached.getComputerIds();

			assertNotNull(gridId);
			assertNotNull(numberOfCpus);
			assertNotNull(params);
			assertNotNull(workflow);
			assertNotNull(computerIds);
			assertTrue(computerIds.size() > 0);

			if (gridId.equals(assignmentDTO.getGridId())
					&& numberOfCpus.equals(assignmentDTO.getNumCPUs())
					&& params.equals(assignmentDTO.getParams())
					&& workflow.equals(assignmentDTO.getWorkflow())) {
				return true;
			}

		}
		return false;
	}
}
