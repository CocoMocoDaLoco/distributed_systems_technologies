package dst.ass1;

import static junit.framework.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;

import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IMembershipKey;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.ModelFactory;
import dst.ass1.jpa.util.JdbcConnection;
import dst.ass1.jpa.util.JdbcHelper;

public abstract class AbstractTest {

	protected EntityManagerFactory emf;
	protected EntityManager em;
	protected JdbcConnection jdbcConnection;
	protected ModelFactory modelFactory;

	@Before
	public void init() throws Exception {
		emf = AbstractTestSuite.getEmf();
		em = emf.createEntityManager();
		jdbcConnection = new JdbcConnection();
		modelFactory = new ModelFactory();

		setUpDatabase();
	}

	@After
	public void clean() throws Exception {
		if (em.getTransaction().isActive())
			em.getTransaction().rollback();
		em.close();
		JdbcHelper.cleanTables(jdbcConnection);
		jdbcConnection.disconnect();
	}

	protected void setUpDatabase() throws Exception {
	}

	protected List<Long> getJobIds(List<IJob> jobs) {
		List<Long> ids = new ArrayList<Long>();

		for (IJob job : jobs)
			ids.add(job.getId());

		return ids;
	}

	protected List<Long> getUserIds(List<IUser> users) {
		List<Long> ids = new ArrayList<Long>();

		for (IUser user : users)
			ids.add(((IPerson) user).getId());

		return ids;
	}

	protected List<Long> getComputerIds(List<IComputer> computers) {
		List<Long> ids = new ArrayList<Long>();

		for (IComputer computer : computers)
			ids.add(computer.getId());

		return ids;
	}

	protected boolean checkMembership(Long userId, Long gridId,
			Double disocunt, List<IMembership> memberships) {

		for (IMembership membership : memberships) {
			IMembershipKey memId = membership.getId();
			assertNotNull(memId);

			IGrid memGrid = memId.getGrid();
			assertNotNull(memGrid);
			Long memGridId = memGrid.getId();
			assertNotNull(memGridId);

			IUser memUser = memId.getUser();
			assertNotNull(memUser);
			Long memUserId = ((IPerson) memUser).getId();
			assertNotNull(memUserId);

			Double memDiscount = membership.getDiscount();
			assertNotNull(memDiscount);

			assertNotNull(membership.getRegistration());

			if (memGridId.equals(gridId) && memUserId.equals(userId)
					&& memDiscount.equals(disocunt))
				return true;
		}

		return false;
	}

}
