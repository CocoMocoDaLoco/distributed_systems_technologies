package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.model.IAddress;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IMembershipKey;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;

public class Test_2a02_4 extends AbstractTest {

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

			em.persist(admin1);
			em.persist(admin2);
			em.persist(user1);
			em.persist(user2);

			IGrid grid = modelFactory.createGrid();
			grid.setName("grid1");
			grid.setLocation("vienna");
			grid.setCostsPerCPUMinute(new BigDecimal(20));

			em.persist(grid);

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

			em.persist(admin1);
			em.persist(admin2);
			em.persist(user1);
			em.persist(user2);
			em.persist(grid);
			em.persist(membership1);
			em.persist(membership2);

			tx.commit();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
