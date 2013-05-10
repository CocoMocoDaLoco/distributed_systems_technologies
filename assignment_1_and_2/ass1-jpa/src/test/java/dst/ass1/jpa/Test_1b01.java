package dst.ass1.jpa;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1b01 extends AbstractTest {

	private Long user1Id;

	@Test
	public void testUserAccountNoBankCodeConstraint() throws SQLException,
			ClassNotFoundException {

		boolean isConstraint = false;
		EntityTransaction tx = em.getTransaction();
		tx.begin();
		try {
			IUser u1 = (new DAOFactory((Session) em.getDelegate()))
					.getUserDAO().findById(user1Id);
			u1.setAccountNo("account2");
			u1.setBankCode("bank2");
			em.persist(u1);
			em.flush();

		} catch (PersistenceException e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				isConstraint = true;
			}

		} finally {
			tx.rollback();
		}

		assertTrue(isConstraint);

	}

	@Test
	public void testUserAccountNoBankCodeConstraintJdbc()
			throws ClassNotFoundException, SQLException {
		String sql = "show index  FROM User where Key_name='accountNo' and Column_name='accountNo' and Non_unique=0";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		rs.close();

		sql = "show index  FROM User where Key_name='accountNo' and Column_name='bankCode' and Non_unique=0";
		stmt = jdbcConnection.getConnection().createStatement();
		rs = stmt.executeQuery(sql);
		assertTrue(rs.next());
		rs.close();

		assertTrue(JdbcHelper.isNullable("User", "accountNo", jdbcConnection));
		assertTrue(JdbcHelper.isNullable("User", "bankCode", jdbcConnection));
	}

	public void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			IUser u1 = modelFactory.createUser();
			IUser u2 = modelFactory.createUser();

			u1.setAccountNo("account1");
			u1.setBankCode("bank1");
			u1.setUsername("u1");

			u2.setAccountNo("account2");
			u2.setBankCode("bank2");
			u2.setUsername("u2");

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
