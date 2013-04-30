package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.model.IUser;

public class Test_2a01_2 extends AbstractTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testNamedQueryFindUsersWithActiveMembership() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createNamedQuery("findUsersWithActiveMembership");
			query.setParameter("name", "grid1");
			query.setParameter("minNr", new Long(2));

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(0, result.size());
		} catch (Exception e) {
			fail(e.getMessage());

		} finally {
			tx.rollback();
		}
	}

}
