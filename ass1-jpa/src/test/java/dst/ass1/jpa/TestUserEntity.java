package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IAddress;
import dst.ass1.jpa.model.IEnvironment;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IMembershipKey;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.util.JdbcHelper;

public class TestUserEntity extends AbstractTest {

	private Long user1Id;
	private Long user2Id;
	private Long job1Id;
	private Long job2Id;
	private Long job3Id;

	@Test
	public void testUser() throws NoSuchAlgorithmException {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IUser> users = daoFactory.getUserDAO().findAll();

		assertNotNull(users);
		assertEquals(2, users.size());

		IUser u1 = users.get(0);
		IUser u2 = users.get(1);
		IPerson p1 = (IPerson) u1;
		IPerson p2 = (IPerson) u2;
		MessageDigest md = MessageDigest.getInstance("MD5");

		assertEquals("account1", u1.getAccountNo());
		assertEquals("bank1", u1.getBankCode());
		assertEquals("user1", u1.getUsername());
		assertEquals("city1", p1.getAddress().getCity());
		assertEquals("street1", p1.getAddress().getStreet());
		assertEquals("zip1", p1.getAddress().getZipCode());
		assertEquals("user1", p1.getFirstName());
		assertEquals("user1", p1.getLastName());
		assertEquals(user1Id, p1.getId());

		byte[] passwd1 = md.digest("pw1".getBytes());
		assertTrue(Arrays.equals(passwd1, u1.getPassword()));

		assertNotNull(u1.getMemberships());
		assertEquals(1, u1.getMemberships().size());
		assertEquals(p1.getId(), ((IPerson) u1.getMemberships().get(0).getId()
				.getUser()).getId());

		assertNotNull(u1.getJobs());
		assertEquals(2, u1.getJobs().size());
		assertEquals(job1Id, u1.getJobs().get(0).getId());
		assertEquals(job3Id, u1.getJobs().get(1).getId());

		assertEquals("account2", u2.getAccountNo());
		assertEquals("bank2", u2.getBankCode());
		assertEquals("user2", u2.getUsername());
		assertEquals("city2", p2.getAddress().getCity());
		assertEquals("street2", p2.getAddress().getStreet());
		assertEquals("zip2", p2.getAddress().getZipCode());
		assertEquals("user2", p2.getFirstName());
		assertEquals("user2", p2.getLastName());
		assertEquals(user2Id, p2.getId());

		byte[] passwd2 = md.digest("pw2".getBytes());
		assertTrue(Arrays.equals(passwd2, u2.getPassword()));

		assertNotNull(u2.getMemberships());
		assertEquals(1, u2.getMemberships().size());
		assertEquals(p2.getId(), ((IPerson) u2.getMemberships().get(0).getId()
				.getUser()).getId());

		assertNotNull(u2.getJobs());
		assertEquals(1, u2.getJobs().size());
		assertEquals(job2Id, u2.getJobs().get(0).getId());

	}

	@Test
	public void testUserJdbc() throws Exception {
		int type = JdbcHelper.getInheritanceType(jdbcConnection, "Person");
		String sql = "";
		switch (type) {
		case 0:
			sql = "SELECT u.id, p.city, p.street, p.zipCode, p.firstName, p.lastName, u.accountNo, u.bankCode, u.password, u.username from User u, Person p where u.id=p.id order by u.id asc";
			break;
		case 1:
			sql = "SELECT id, city, street, zipCode, firstName, lastName, accountNo, bankCode, password, username from User order by id asc";
			break;
		default:
			throw new Exception("unknown inheritance type");
		}

		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		assertTrue(rs.next());

		assertEquals((long) user1Id, rs.getLong("id"));
		assertEquals("city1", rs.getString("city"));
		assertEquals("street1", rs.getString("street"));
		assertEquals("zip1", rs.getString("zipCode"));
		assertEquals("user1", rs.getString("firstName"));
		assertEquals("user1", rs.getString("lastName"));
		assertEquals("account1", rs.getString("accountNo"));
		assertEquals("bank1", rs.getString("bankCode"));
		assertEquals("user1", rs.getString("username"));
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] passwd = md.digest(("pw1").getBytes());
		assertTrue(Arrays.equals(passwd, rs.getBytes("password")));
		
		assertTrue(rs.next());

		assertEquals((long) user2Id, rs.getLong("id"));
		assertEquals("city2", rs.getString("city"));
		assertEquals("street2", rs.getString("street"));
		assertEquals("zip2", rs.getString("zipCode"));
		assertEquals("user2", rs.getString("firstName"));
		assertEquals("user2", rs.getString("lastName"));
		assertEquals("account2", rs.getString("accountNo"));
		assertEquals("bank2", rs.getString("bankCode"));
		assertEquals("user2", rs.getString("username"));
		md = MessageDigest.getInstance("MD5");
		passwd = md.digest(("pw2").getBytes());
		assertTrue(Arrays.equals(passwd, rs.getBytes("password")));

		assertFalse(rs.next());

		rs.close();
	}

	protected void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IGrid grid = modelFactory.createGrid();

			IAddress address3 = modelFactory.createAddress();
			IAddress address4 = modelFactory.createAddress();

			address3.setCity("city1");
			address3.setStreet("street1");
			address3.setZipCode("zip1");

			address4.setCity("city2");
			address4.setStreet("street2");
			address4.setZipCode("zip2");

			MessageDigest md = MessageDigest.getInstance("MD5");

			IUser user1 = modelFactory.createUser();
			((IPerson) user1).setFirstName("user1");
			((IPerson) user1).setLastName("user1");
			((IPerson) user1).setAddress(address3);
			user1.setAccountNo("account1");
			user1.setUsername("user1");
			user1.setBankCode("bank1");
			user1.setPassword(md.digest("pw1".getBytes()));

			IUser user2 = modelFactory.createUser();
			((IPerson) user2).setFirstName("user2");
			((IPerson) user2).setLastName("user2");
			((IPerson) user2).setAddress(address4);
			user2.setAccountNo("account2");
			user2.setUsername("user2");
			user2.setBankCode("bank2");
			user2.setPassword(md.digest("pw2".getBytes()));

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

			IEnvironment environment1 = modelFactory.createEnvironment();
			IEnvironment environment2 = modelFactory.createEnvironment();
			IEnvironment environment3 = modelFactory.createEnvironment();

			em.persist(environment1);
			em.persist(environment2);
			em.persist(environment3);

			IExecution ex1 = modelFactory.createExecution();
			IExecution ex2 = modelFactory.createExecution();
			IExecution ex3 = modelFactory.createExecution();

			IJob job1 = modelFactory.createJob();
			job1.setNumCPUs(2);
			job1.setExecutionTime(0);
			job1.setEnvironment(environment1);
			job1.setExecution(ex1);
			job1.setUser(user1);
			user1.addJob(job1);

			IJob job2 = modelFactory.createJob();
			job2.setNumCPUs(3);
			job2.setExecutionTime(0);
			job2.setEnvironment(environment2);
			job2.setExecution(ex2);
			job2.setUser(user2);
			user2.addJob(job2);

			IJob job3 = modelFactory.createJob();
			job3.setNumCPUs(4);
			job3.setExecutionTime(0);
			job3.setEnvironment(environment3);
			job3.setExecution(ex3);
			job3.setUser(user1);
			user1.addJob(job3);

			em.persist(ex1);
			em.persist(ex2);
			em.persist(ex3);

			em.persist(grid);
			em.persist(user1);
			em.persist(user2);
			em.persist(job1);
			em.persist(job2);
			em.persist(job3);
			em.persist(membership1);
			em.persist(membership2);

			tx.commit();

			user1Id = ((IPerson) user1).getId();
			user2Id = ((IPerson) user2).getId();
			job1Id = job1.getId();
			job2Id = job2.getId();
			job3Id = job3.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
