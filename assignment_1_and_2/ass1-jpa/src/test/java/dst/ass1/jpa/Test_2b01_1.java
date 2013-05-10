package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IComputer;

public class Test_2b01_1 extends AbstractTest {

	private TestData testData;

	@Before
	public void setUp() throws NoSuchAlgorithmException {
		testData = new TestData(em);
		testData.insertTestData();
	}

	@Test
	public void testFindComputersInViennaNamedQueryExists() {
		boolean exists = false;
		try {
			em.createNamedQuery("findComputersInVienna");
			exists = true;
		} catch (Exception e) {
			exists = false;
		}
		assertTrue(exists);
	}

	@Test
	public void testFindComputersInVienna() {

		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());
		HashMap<IComputer, Integer> usageMap = daoFactory.getComputerDAO()
				.findComputersInViennaUsage();
		assertEquals(3, usageMap.size());

		boolean isId1 = false;
		boolean isId2 = false;
		boolean isId3 = false;

		Iterator<?> it = usageMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<?, ?> pairs = (Map.Entry<?, ?>) it.next();
			long id = ((IComputer) pairs.getKey()).getId().longValue();
			int usage = ((Integer) pairs.getValue()).intValue();

			if (id == testData.computer1Id) {
				isId1 = true;
				assertEquals(36000000, usage);
			} else if (id == testData.computer2Id) {
				isId2 = true;
				assertEquals(0, usage);

			} else if (id == testData.computer3Id) {
				isId3 = true;
				assertEquals(0, usage);
			}
		}

		assertTrue(isId1);
		assertTrue(isId2);
		assertTrue(isId3);
	}
}
