package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IAddress;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.util.JdbcHelper;

public class TestAdminEntity extends AbstractTest {

	private Long admin1Id;
	private Long admin2Id;
	private Long cluster1Id;
	private Long cluster2Id;

	@Test
	public void testAdmin() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IAdmin> admins = daoFactory.getAdminDAO().findAll();

		assertNotNull(admins);
		assertEquals(2, admins.size());

		IAdmin a1 = daoFactory.getAdminDAO().findById(admin1Id);
		IAdmin a2 = daoFactory.getAdminDAO().findById(admin2Id);

		IPerson p1 = (IPerson) a1;
		IPerson p2 = (IPerson) a2;

		assertEquals(admin1Id, p1.getId());
		assertEquals("city1", p1.getAddress().getCity());
		assertEquals("street1", p1.getAddress().getStreet());
		assertEquals("zip1", p1.getAddress().getZipCode());
		assertEquals("admin1", p1.getFirstName());
		assertEquals("admin1", p1.getLastName());

		assertNotNull(a1.getClusters());
		assertEquals(1, a1.getClusters().size());
		assertEquals(cluster1Id, a1.getClusters().get(0).getId());

		assertEquals(admin2Id, p2.getId());
		assertEquals("city2", p2.getAddress().getCity());
		assertEquals("street2", p2.getAddress().getStreet());
		assertEquals("zip2", p2.getAddress().getZipCode());
		assertEquals("admin2", p2.getFirstName());
		assertEquals("admin2", p2.getLastName());

		assertNotNull(a2.getClusters());
		assertEquals(1, a2.getClusters().size());
		assertEquals(cluster2Id, a2.getClusters().get(0).getId());

	}

	@Test
	public void testAdminEntityJdbc() throws Exception {
		int type = JdbcHelper.getInheritanceType(jdbcConnection, "Person");
		String sql = "";
		switch (type) {
		case 0:
			sql = "SELECT a.id, p.city, p.street, p.zipCode, p.firstName, p.lastName from Admin a, Person p where a.id = p.id order by a.id asc";
			break;
		case 1:
			sql = "SELECT id, city, street, zipCode, firstName, lastName from Admin order by id asc";
			break;
		default:
			throw new Exception("unkown inheritance type");
		}

		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while (rs.next()) {

			long id = rs.getLong("id");

			if (id == admin1Id.longValue()) {
				assertEquals("city1", rs.getString("city"));
				assertEquals("street1", rs.getString("street"));
				assertEquals("zip1", rs.getString("zipCode"));
				assertEquals("admin1", rs.getString("firstName"));
				assertEquals("admin1", rs.getString("lastName"));
			} else if (id == admin2Id.longValue()) {
				assertEquals("city2", rs.getString("city"));
				assertEquals("street2", rs.getString("street"));
				assertEquals("zip2", rs.getString("zipCode"));
				assertEquals("admin2", rs.getString("firstName"));
				assertEquals("admin2", rs.getString("lastName"));
			} else {
				fail("Unexpected Admin found!");
			}

		}

		rs.close();
		stmt.close();
	}

	protected void setUpDatabase() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IAddress address1 = modelFactory.createAddress();
			IAddress address2 = modelFactory.createAddress();
			IAddress address3 = modelFactory.createAddress();
			IAddress address4 = modelFactory.createAddress();

			address1.setCity("city1");
			address1.setStreet("street1");
			address1.setZipCode("zip1");

			address2.setCity("city2");
			address2.setStreet("street2");
			address2.setZipCode("zip2");

			address3.setCity("city3");
			address3.setStreet("street3");
			address3.setZipCode("zip3");

			address4.setCity("city4");
			address4.setStreet("street4");
			address4.setZipCode("zip4");

			IAdmin admin1 = modelFactory.createAdmin();
			IAdmin admin2 = modelFactory.createAdmin();

			((IPerson) admin1).setFirstName("admin1");
			((IPerson) admin1).setLastName("admin1");
			((IPerson) admin1).setAddress(address1);

			((IPerson) admin2).setFirstName("admin2");
			((IPerson) admin2).setLastName("admin2");
			((IPerson) admin2).setAddress(address2);

			IGrid grid = modelFactory.createGrid();

			ICluster cluster1 = modelFactory.createCluster();
			cluster1.setAdmin(admin1);
			cluster1.setName("cluster1");
			cluster1.setLastService(new Date());
			cluster1.setNextService(new Date());
			cluster1.setGrid(grid);

			ICluster cluster2 = modelFactory.createCluster();
			cluster2.setAdmin(admin2);
			cluster2.setName("cluster2");
			cluster2.setLastService(new Date());
			cluster2.setNextService(new Date());
			cluster2.setGrid(grid);

			cluster1.addComposedOf(cluster2);
			cluster2.addPartOf(cluster1);

			admin1.addCluster(cluster1);
			admin2.addCluster(cluster2);

			em.persist(grid);
			em.persist(admin1);
			em.persist(admin2);
			em.persist(cluster1);
			em.persist(cluster2);

			tx.commit();

			admin1Id = ((IPerson) admin1).getId();
			admin2Id = ((IPerson) admin2).getId();

			cluster1Id = cluster1.getId();
			cluster2Id = cluster2.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
