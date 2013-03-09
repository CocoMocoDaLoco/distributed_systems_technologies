package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.dao.IGridDAO;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IGrid;

public class TestGridEntity extends AbstractTest {

	private Long gridId;

	@Test
	public void testGrid() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);
		IGridDAO gridDAO = daoFactory.getGridDAO();
		IGrid grid = gridDAO.findById(new Long(1));

		assertNotNull(grid);
		assertTrue(grid.getId().equals(gridId));
		assertEquals("grid1", grid.getName());
		assertEquals("vienna", grid.getLocation());
		assertTrue((new BigDecimal(20)).compareTo(grid.getCostsPerCPUMinute()) == 0);

		assertNotNull(grid.getClusters());
		assertEquals(2, grid.getClusters().size());
	}

	@Test
	public void testGridJdbc() throws ClassNotFoundException, SQLException {
		String sql = "select id, costsPerCPUMinute, location, name from Grid order by id asc";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		assertTrue(rs.next());

		assertEquals((long) gridId, rs.getLong("id"));
		assertTrue((new BigDecimal(20.00).compareTo(rs
				.getBigDecimal("costsPerCPUMinute")) == 0));
		assertEquals("vienna", rs.getString("location"));
		assertEquals("grid1", rs.getString("name"));

		assertFalse(rs.next());

		rs.close();
	}

	protected void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			
			IAdmin admin1 = modelFactory.createAdmin();

			ICluster cluster1 = modelFactory.createCluster();
			cluster1.setAdmin(admin1);
			cluster1.setName("cluster1");
			cluster1.setLastService(new Date());
			cluster1.setNextService(new Date());

			ICluster cluster2 = modelFactory.createCluster();
			cluster2.setAdmin(admin1);
			cluster2.setName("cluster2");
			cluster2.setLastService(new Date());
			cluster2.setNextService(new Date());

			IGrid grid = modelFactory.createGrid();
			grid.setName("grid1");
			grid.setLocation("vienna");
			grid.setCostsPerCPUMinute(new BigDecimal(20));

			grid.addCluster(cluster1);
			grid.addCluster(cluster2);

			cluster1.setGrid(grid);
			cluster2.setGrid(grid);

			em.persist(admin1);
			em.persist(grid);
			em.persist(cluster1);
			em.persist(cluster2);

			tx.commit();

			gridId = grid.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}

}
