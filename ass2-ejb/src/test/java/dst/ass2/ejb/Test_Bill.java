package dst.ass2.ejb;

import static dst.ass2.ejb.util.EJBUtils.lookup;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import dst.ass1.jpa.util.JdbcHelper;
import dst.ass2.AbstractEJBTest;
import dst.ass2.ejb.dto.BillDTO;
import dst.ass2.ejb.dto.BillDTO.BillPerJob;
import dst.ass2.ejb.session.JobManagementBean;
import dst.ass2.ejb.session.interfaces.IJobManagementBean;

public class Test_Bill extends AbstractEJBTest {

	@Before
	public void setUp() {
		managementBean.clearPriceCache();
	}

	@After
	public void tearDown() {
	}

	@Test
	public void testGetBill_forUser_Hansi() {
		try {
			// get all available grid-ids from db
			List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();

			assertEquals(2, gridIds.size());

			// add prices to management bean
			managementBean_addPrices();

			// add jobs to job management bean
			jobmanagementBean_addJobs(gridIds);

			// finish all jobs on db level!
			JdbcHelper.finishAllJobs(jdbcConnection);

			Future<BillDTO> result = managementBean.getBillForUser("hansi");
			while (!result.isDone()) {
				Thread.sleep(100);
			}

			// gather all paid jobs for user
			List<Long> jobIds = jdbcTestUtil
					.getPaidJobsForUser_FROM_DB("hansi");

			assertEquals(3, jobIds.size());

			BillDTO billDTO = result.get();
			assertNotNull(billDTO);

			List<BillPerJob> bills = billDTO.getBills();
			assertNotNull(bills);
			assertEquals(3, bills.size());

			Map<Long, BillPerJob> temp = createMap(bills);

			BillPerJob bill = temp.get(jobIds.get(0));

			assertNotNull(bill);
			assertNotNull(bill.getNumberOfComputers());
			assertTrue(bill.getNumberOfComputers().intValue() >= 1);
			assertNotNull(bill.getSetupCosts());
			assertTrue(bill.getSetupCosts().compareTo(new BigDecimal(45.00)) == 0);
			assertNotNull(bill.getExecutionCosts());
			assertTrue(bill.getExecutionCosts().compareTo(
					new BigDecimal(135.00)) == 0);
			assertNotNull(bill.getJobCosts());
			assertTrue(bill.getJobCosts().compareTo(new BigDecimal(180.00)) == 0);

			bill = temp.get(jobIds.get(1));

			assertNotNull(bill);
			assertNotNull(bill.getNumberOfComputers());
			assertTrue(bill.getNumberOfComputers().intValue() >= 1);
			assertNotNull(bill.getSetupCosts());
			assertTrue(bill.getSetupCosts().compareTo(new BigDecimal(40.50)) == 0);
			assertNotNull(bill.getExecutionCosts());
			assertTrue(bill.getExecutionCosts().compareTo(new BigDecimal(0.00)) == 0);
			assertNotNull(bill.getJobCosts());
			assertTrue(bill.getJobCosts().compareTo(new BigDecimal(40.50)) == 0);

			bill = temp.get(jobIds.get(2));

			assertNotNull(bill);
			assertNotNull(bill.getNumberOfComputers());
			assertTrue(bill.getNumberOfComputers().intValue() >= 1);
			assertNotNull(bill.getSetupCosts());
			assertTrue(bill.getSetupCosts().compareTo(new BigDecimal(28.00)) == 0);
			assertNotNull(bill.getExecutionCosts());
			assertTrue(bill.getExecutionCosts().compareTo(new BigDecimal(0.00)) == 0);
			assertNotNull(bill.getJobCosts());
			assertTrue(bill.getJobCosts().compareTo(new BigDecimal(28.00)) == 0);

			assertNotNull(billDTO.getTotalPrice());
			assertTrue(billDTO.getTotalPrice()
					.compareTo(new BigDecimal(248.50)) == 0);
			assertNotNull(billDTO.getUsername());
			assertTrue(billDTO.getUsername().equals("hansi"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("Unexpected Exception: %s !", e.getMessage()));
		}

	}

	@Test
	public void testGetBill_forUser_Franz() {
		try {
			// get all available grid-ids from db
			List<Long> gridIds = jdbcTestUtil.getAllGridIds_FROM_DB();

			assertEquals(2, gridIds.size());

			// add prices to management bean
			managementBean_addPrices();

			// add jobs for user franz
			IJobManagementBean jobManagementBean = lookup(ctx,
					JobManagementBean.class);

			jobManagementBean.login("franz", "liebe");

			List<String> params3 = new ArrayList<String>();
			params3.add("param1");
			params3.add("param2");
			params3.add("param3");
			params3.add("param4");
			jobManagementBean.addJob(gridIds.get(0), new Integer(2),
					"workflow3", params3);

			List<String> params5 = new ArrayList<String>();

			jobManagementBean.addJob(gridIds.get(0), new Integer(3),
					"workflow5", params5);

			jobManagementBean.submitAssignments();

			// finish all jobs on db level!
			JdbcHelper.finishAllJobs(jdbcConnection);

			Future<BillDTO> result = managementBean.getBillForUser("franz");

			while (!result.isDone()) {
				Thread.sleep(100);
			}

			// gather all paid jobs for user
			List<Long> jobIds = jdbcTestUtil
					.getPaidJobsForUser_FROM_DB("franz");
			assertEquals(2, jobIds.size());

			BillDTO billDTO = result.get();
			assertNotNull(billDTO);

			List<BillPerJob> bills = billDTO.getBills();
			assertNotNull(bills);

			assertEquals(2, bills.size());

			Map<Long, BillPerJob> temp = createMap(bills);

			BillPerJob bill = temp.get(jobIds.get(0));

			assertNotNull(bill);
			assertNotNull(bill.getNumberOfComputers());
			assertTrue(bill.getNumberOfComputers().intValue() >= 1);
			assertNotNull(bill.getSetupCosts());
			assertTrue(bill.getSetupCosts().compareTo(new BigDecimal(40.00)) == 0);
			assertNotNull(bill.getExecutionCosts());
			assertTrue(bill.getExecutionCosts().compareTo(new BigDecimal(0.00)) == 0);
			assertNotNull(bill.getJobCosts());
			assertTrue(bill.getJobCosts().compareTo(new BigDecimal(40.00)) == 0);

			bill = temp.get(jobIds.get(1));

			assertNotNull(bill);
			assertNotNull(bill.getNumberOfComputers());
			assertTrue(bill.getNumberOfComputers().intValue() >= 1);
			assertNotNull(bill.getSetupCosts());
			assertTrue(bill.getSetupCosts().compareTo(new BigDecimal(36.00)) == 0);
			assertNotNull(bill.getExecutionCosts());
			assertTrue(bill.getExecutionCosts().compareTo(new BigDecimal(0.00)) == 0);
			assertNotNull(bill.getJobCosts());
			assertTrue(bill.getJobCosts().compareTo(new BigDecimal(36.00)) == 0);

			assertNotNull(billDTO.getTotalPrice());
			assertTrue(billDTO.getTotalPrice().compareTo(new BigDecimal(76.00)) == 0);
			assertNotNull(billDTO.getUsername());
			assertTrue(billDTO.getUsername().equals("franz"));

		} catch (Exception e) {
			e.printStackTrace();
			fail(String.format("Unexpected Exception: %s !", e.getMessage()));
		}
	}

	@Test
	public void testGetBill_ForNotExistingUser() {
		String username = "not_existing_user";

		try {
			Future<BillDTO> result = managementBean.getBillForUser(username);

			while (!result.isDone()) {
				Thread.sleep(100);
			}

			result.get();

			fail(String.format("User: %s should not exist!", username));
		} catch (Exception e) {
			// Expected exception since user should not exist!
		}

	}

	private Map<Long, BillPerJob> createMap(List<BillPerJob> bills) {
		Map<Long, BillPerJob> ret = new HashMap<Long, BillPerJob>();

		for (BillPerJob bill : bills)
			ret.put(bill.getJobId(), bill);

		return ret;
	}

}
