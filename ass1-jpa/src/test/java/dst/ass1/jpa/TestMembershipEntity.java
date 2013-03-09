package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IMembershipKey;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;

public class TestMembershipEntity extends AbstractTest {

	private Long gridId;
	private Long user1Id;
	private Long user2Id;

	@Test
	public void testMembership() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IMembership> memberships = daoFactory.getMembershipDAO().findAll();

		assertNotNull(memberships);
		assertEquals(2, memberships.size());

		IMembership m1 = memberships.get(0);
		IMembership m2 = memberships.get(1);

		assertNotNull(m1.getId());
		assertNotNull(m1.getId().getGrid());
		assertEquals(gridId, m1.getId().getGrid().getId());
		assertNotNull(m1.getId().getUser());
		assertEquals(user1Id, ((IPerson) m1.getId().getUser()).getId());
		assertEquals(new Double(0.1), m1.getDiscount());
		assertNotNull(m1.getRegistration());

		assertNotNull(m2.getId());
		assertNotNull(m2.getId().getGrid());
		assertEquals(gridId, m2.getId().getGrid().getId());
		assertNotNull(m2.getId().getUser());
		assertEquals(user2Id, ((IPerson) m2.getId().getUser()).getId());
		assertEquals(new Double(0.1), m2.getDiscount());
		assertNotNull(m2.getRegistration());
	}

	@Test
	public void testMembershipJdbc() throws ClassNotFoundException,
			SQLException {
		String sql = "SELECT discount, registration, grid_id, user_id from Membership";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		assertTrue(rs.next());

		assertTrue((new Double(0.1)).compareTo(rs.getDouble("discount")) == 0);
		assertNotNull(rs.getDate("registration"));
		assertEquals((long) gridId, rs.getLong("grid_id"));
		assertEquals((long) user1Id, rs.getLong("user_id"));

		assertTrue(rs.next());

		assertTrue((new Double(0.1)).compareTo(rs.getDouble("discount")) == 0);
		assertNotNull(rs.getDate("registration"));
		assertEquals((long) gridId, rs.getLong("grid_id"));
		assertEquals((long) user2Id, rs.getLong("user_id"));

		assertFalse(rs.next());

		rs.close();

	}

	protected void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IGrid grid = modelFactory.createGrid();
			IUser user1 = modelFactory.createUser();
			IUser user2 = modelFactory.createUser();
			user1.setUsername("u1");
			user2.setUsername("u2");

			IMembership membership1 = modelFactory.createMembership();
			membership1.setDiscount(0.10);
			membership1.setRegistration(new Date());

			IMembershipKey key1 = modelFactory.createMembershipKey();
			key1.setUser(user1);
			key1.setGrid(grid);

			membership1.setId(key1);
			user1.addMembership(membership1);

			IMembership membership2 = modelFactory.createMembership();
			membership2.setDiscount(0.10);
			membership2.setRegistration(new Date());

			IMembershipKey key2 = modelFactory.createMembershipKey();
			key2.setUser(user2);
			key2.setGrid(grid);

			membership2.setId(key2);
			user2.addMembership(membership2);

			em.persist(grid);
			em.persist(user1);
			em.persist(user2);
			em.persist(membership1);
			em.persist(membership2);

			tx.commit();

			gridId = grid.getId();
			user1Id = ((IPerson) user1).getId();
			user2Id = ((IPerson) user2).getId();
		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
