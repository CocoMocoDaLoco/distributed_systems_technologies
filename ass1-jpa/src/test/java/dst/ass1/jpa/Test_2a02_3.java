package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.util.List;

import javax.persistence.Query;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.model.IUser;

public class Test_2a02_3 extends AbstractTest {

	@SuppressWarnings("unchecked")
	@Test
	public void testFindMostActiveUser() {
		try {

			Query query = em.createNamedQuery("findMostActiveUser");

			List<IUser> result = (List<IUser>) query.getResultList();
			assertNotNull(result);
			assertEquals(0, result.size());
		} catch (Exception e) {
			fail(e.getMessage());
		}

	}
}
