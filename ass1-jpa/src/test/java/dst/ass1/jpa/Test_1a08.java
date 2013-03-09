package dst.ass1.jpa;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.persistence.EntityTransaction;

import static org.junit.Assert.*;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IEnvironment;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a08 extends AbstractTest {

	@Test
	public void testEnvironmentOrderPreserved() throws NoSuchAlgorithmException {
		final int PARAM_COUNT = 1000;
		IEnvironment env = modelFactory.createEnvironment();

		ArrayList<String> params = new ArrayList<String>();
		MessageDigest md = MessageDigest.getInstance("MD5");
		for (int i = 0; i < PARAM_COUNT; i++) {
			params.add(String.valueOf(md.digest(String.valueOf(Math.random())
					.getBytes())));
		}

		env.setParams(params);

		EntityTransaction tx = em.getTransaction();
		tx.begin();
		em.persist(env);
		tx.commit();

		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());
		env = daoFactory.getEnvironmentDAO().findById(env.getId());

		assertTrue(params.equals(env.getParams()));
	}

	@Test
	public void testEnvironmentOrderPreservedJdbc()
			throws ClassNotFoundException, SQLException {
		assertTrue(JdbcHelper.isColumnInTable("Environment_params",
				"params_ORDER", jdbcConnection));
	}
}
