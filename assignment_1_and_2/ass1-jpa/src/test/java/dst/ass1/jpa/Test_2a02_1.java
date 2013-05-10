package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;

public class Test_2a02_1 extends AbstractTest {

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
			Query query = em.createNamedQuery("findMostActiveUser");

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(1, result.size());

			IUser u1 = result.get(0);
			assertEquals(testData.user1Id, ((IPerson) u1).getId());

		} catch (Exception e) {
			fail(e.getMessage());
		} finally {
			tx.rollback();
		}

	}
}
