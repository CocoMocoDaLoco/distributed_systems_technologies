package dst.ass1.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.listener.DefaultListener;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.IPerson;

public class Test_4c extends AbstractTest {

	@Test
	public void testDefaultListener() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		try {
			assertEquals(1, DefaultListener.getRemoveOperations());
			assertTrue(DefaultListener.getPersistOperations() > 0);
			assertEquals(
					new Double(DefaultListener.getAverageTimeToPersist()),
					new Double((double) DefaultListener
							.getOverallTimeToPersist()
							/ DefaultListener.getPersistOperations()));

			List<IAdmin> admins = daoFactory.getAdminDAO().findAll();
			assertNotNull(admins);
			assertTrue(admins.size() > 0);

			int loadOperations = DefaultListener.getLoadOperations();
			em.refresh(admins.get(0));
			assertEquals(loadOperations + 1,
					DefaultListener.getLoadOperations());

			EntityTransaction tx = em.getTransaction();
			tx.begin();

			admins = daoFactory.getAdminDAO().findAll();
			assertNotNull(admins);
			assertTrue(admins.size() > 0);

			((IPerson) admins.get(0)).setFirstName("updated");
			em.persist(admins.get(0));

			tx.commit();

			assertEquals(1, DefaultListener.getUpdateOperations());

		} catch (Exception e) {
			fail(e.getMessage());
		}

	}

	protected void setUpDatabase() throws NoSuchAlgorithmException,
			ClassNotFoundException, SQLException {

		DefaultListener.clear();

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IAdmin admin1 = modelFactory.createAdmin();
			((IPerson) admin1).setFirstName("admin");
			em.persist(admin1);
			em.flush();
			em.remove(admin1);

			admin1 = modelFactory.createAdmin();
			((IPerson) admin1).setFirstName("admin");
			em.persist(admin1);

			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());

		}
	}
}
