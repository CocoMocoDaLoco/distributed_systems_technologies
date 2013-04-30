package dst.ass1.jpa;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.util.Date;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IGrid;

public class Test_4b extends AbstractTest {

	private Long computerId;

	@Test
	public void testEntityListener() {
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {

			IComputer computer = daoFactory.getComputerDAO().findById(
					computerId);
			assertNotNull(computer);

			computer.setLocation("AUT-VIE@1160");
			Date lastUpdate = computer.getLastUpdate();
			try {
				// for Testing
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			em.flush();

			assertTrue(computer.getLastUpdate().after(lastUpdate));

			tx.rollback();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}

	protected void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IAdmin admin1 = modelFactory.createAdmin();

			IComputer computer1 = modelFactory.createComputer();
			computer1.setName("computer1");
			computer1.setCpus(2);
			computer1.setLocation("AUT-VIE-location1");
			computer1.setCreation(new Date(0));
			computer1.setLastUpdate(new Date(0));

			ICluster cluster1 = modelFactory.createCluster();
			cluster1.setAdmin(admin1);
			cluster1.setName("cluster1");
			cluster1.setLastService(new Date());
			cluster1.setNextService(new Date());

			admin1.addCluster(cluster1);

			cluster1.addComputer(computer1);
			computer1.setCluster(cluster1);

			IGrid grid = modelFactory.createGrid();
			cluster1.setGrid(grid);

			em.persist(grid);
			em.persist(admin1);
			em.persist(cluster1);
			em.persist(computer1);

			tx.commit();

			computerId = computer1.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
