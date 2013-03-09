package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityTransaction;

import org.hibernate.Session;
import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IEnvironment;

public class TestEnvironmentEntity extends AbstractTest {

	private Long environment1Id;
	private Long environment2Id;
	private Long environment3Id;

	@Test
	public void testEnvironment() {
		Session session = (Session) em.getDelegate();
		DAOFactory daoFactory = new DAOFactory(session);

		List<IEnvironment> environments = daoFactory.getEnvironmentDAO()
				.findAll();

		assertNotNull(environments);
		assertEquals(3, environments.size());

		IEnvironment e1 = environments.get(0);
		IEnvironment e2 = environments.get(1);
		IEnvironment e3 = environments.get(2);

		assertEquals(environment1Id, e1.getId());
		assertEquals("workflow1", e1.getWorkflow());
		assertNotNull(e1.getParams());
		assertEquals(3, e1.getParams().size());
		assertEquals("param1", e1.getParams().get(0));
		assertEquals("param2", e1.getParams().get(1));
		assertEquals("param3", e1.getParams().get(2));

		assertEquals(environment2Id, e2.getId());
		assertEquals("workflow3", e2.getWorkflow());
		assertNotNull(e2.getParams());
		assertEquals(1, e2.getParams().size());
		assertEquals("param4", e2.getParams().get(0));

		assertEquals(environment3Id, e3.getId());
		assertNull(e3.getWorkflow());
		assertNotNull(e3.getParams());
		assertEquals(1, e3.getParams().size());
		assertEquals("param5", e3.getParams().get(0));

	}

	@Test
	public void testEnvironmentJdbc() throws ClassNotFoundException,
			SQLException {
		String sql = "select id, workflow from Environment order by id asc";
		Statement stmt = jdbcConnection.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		assertTrue(rs.next());

		assertEquals((long) environment1Id, rs.getLong("id"));
		assertEquals("workflow1", rs.getString("workflow"));

		assertTrue(rs.next());

		assertEquals((long) environment2Id, rs.getLong("id"));
		assertEquals("workflow3", rs.getString("workflow"));

		assertTrue(rs.next());

		assertEquals((long) environment3Id, rs.getLong("id"));
		assertEquals(null, rs.getString("workflow"));

		assertFalse(rs.next());

		rs.close();
	}

	protected void setUpDatabase() {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			IEnvironment environment1 = modelFactory.createEnvironment();
			environment1.setWorkflow("workflow1");
			environment1.addParam("param1");
			environment1.addParam("param2");
			environment1.addParam("param3");

			IEnvironment environment2 = modelFactory.createEnvironment();
			environment2.setWorkflow("workflow2");
			environment2.addParam("param4");

			IEnvironment environment3 = modelFactory.createEnvironment();
			environment2.setWorkflow("workflow3");
			environment3.addParam("param5");

			em.persist(environment1);
			em.persist(environment2);
			em.persist(environment3);

			tx.commit();

			environment1Id = environment1.getId();
			environment2Id = environment2.getId();
			environment3Id = environment3.getId();

		} catch (Exception e) {
			tx.rollback();
			fail(e.getMessage());
		}
	}
}
