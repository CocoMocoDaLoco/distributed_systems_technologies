package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.junit.Before;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.model.IUser;

public class Test_2a01_1 extends AbstractTest {

	private TestData testData;

	@Before
	public void setUp() throws NoSuchAlgorithmException {
		testData = new TestData(em);
		testData.insertTestData();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNamedQueryFindUsersWithActiveMembership1() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createNamedQuery("findUsersWithActiveMembership");
			query.setParameter("name", "grid1");
			query.setParameter("minNr", new Long(2));

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(1, result.size());

			assertTrue(getUserIds(result).contains(testData.user1Id));

		} catch (Exception e) {
			fail(e.getMessage());

		} finally {
			tx.rollback();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNamedQueryFindUsersWithActiveMembership2() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createNamedQuery("findUsersWithActiveMembership");
			query.setParameter("name", "grid");
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

	@SuppressWarnings("unchecked")
	@Test
	public void testNamedQueryFindUsersWithActiveMembership3() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createNamedQuery("findUsersWithActiveMembership");
			query.setParameter("name", "grid1");
			query.setParameter("minNr", new Long(0));

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(2, result.size());

			List<Long> ids = getUserIds(result);
			assertTrue(ids.contains(testData.user1Id));
			assertTrue(ids.contains(testData.user2Id));

		} catch (Exception e) {
			fail(e.getMessage());

		} finally {
			tx.rollback();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNamedQueryFindUsersWithActiveMembership4() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createNamedQuery("findUsersWithActiveMembership");
			query.setParameter("name", "grid1");
			query.setParameter("minNr", new Long(1));

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(2, result.size());

			List<Long> ids = getUserIds(result);
			assertTrue(ids.contains(testData.user1Id));
			assertTrue(ids.contains(testData.user2Id));

		} catch (Exception e) {
			fail(e.getMessage());

		} finally {
			tx.rollback();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNamedQueryFindUsersWithActiveMembership5() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createNamedQuery("findUsersWithActiveMembership");
			query.setParameter("name", "grid1");
			query.setParameter("minNr", new Long(3));

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(0, result.size());

		} catch (Exception e) {
			fail(e.getMessage());

		} finally {
			tx.rollback();
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testNamedQueryFindUsersWithActiveMembership6() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Query query = em.createNamedQuery("findUsersWithActiveMembership");
			query.setParameter("name", "grid2");
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
