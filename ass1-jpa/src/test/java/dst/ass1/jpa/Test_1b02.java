package dst.ass1.jpa;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.sql.SQLException;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1b02 extends AbstractTest {

	private Long user1Id;

	@Test
	public void testUserUsernameConstraint() throws SQLException,
			ClassNotFoundException {
		boolean isConstraint = false;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			IUser u1 = (new DAOFactory((Session) em.getDelegate()))
					.getUserDAO().findById(user1Id);
			u1.setUsername("user2");
			em.persist(u1);
			em.flush();

		} catch (PersistenceException e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				isConstraint = true;
			}

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		assertTrue(isConstraint);
	}

	@Test
	public void testUserUsernameConstraintJdbc() throws ClassNotFoundException,
			SQLException {
		assertTrue(JdbcHelper
				.isIndex("User", "username", false, jdbcConnection));
	}

	@Test
	public void testUserNotNullConstraint() {
		boolean isConstraint = false;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			IUser u1 = (new DAOFactory((Session) em.getDelegate()))
					.getUserDAO().findById(user1Id);
			u1.setUsername(null);
			em.persist(u1);
			em.flush();

		} catch (PersistenceException e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				isConstraint = true;
			}

		} finally {
			if (tx.isActive()) {
				tx.rollback();
			}
		}

		assertTrue(isConstraint);
	}

	@Test
	public void testUserNotNullConstraintJdbc() throws ClassNotFoundException,
			SQLException {
		assertFalse(JdbcHelper.isNullable("User", "username", jdbcConnection));
	}

	public void setUpDatabase() {
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			IUser u1 = modelFactory.createUser();
			IUser u2 = modelFactory.createUser();

			u1.setAccountNo("account1");
			u1.setBankCode("bank1");
			u1.setUsername("user1");

			u2.setAccountNo("account2");
			u2.setBankCode("bank2");
			u2.setUsername("user2");

			em.persist(u1);
			em.persist(u2);

			tx.commit();

			user1Id = ((IPerson) u1).getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
