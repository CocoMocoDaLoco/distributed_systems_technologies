package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.HashMap;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IComputer;

public class Test_2b01_2 extends AbstractTest {	
	
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
		
		DAOFactory daoFactory = new DAOFactory((Session)em.getDelegate());
		HashMap<IComputer, Integer> usageMap = daoFactory.getComputerDAO().findComputersInViennaUsage();
		assertEquals(0, usageMap.size());
	}
}
