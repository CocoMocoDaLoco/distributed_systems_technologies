package dst.ass1;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.Before;

import dst.ass1.jpa.model.ModelFactory;
import dst.ass1.jpa.util.JdbcConnection;
import dst.ass1.jpa.util.JdbcHelper;

public abstract class AbstractTest {

	protected EntityManagerFactory emf;
	protected EntityManager em;
	protected JdbcConnection jdbcConnection;
	protected ModelFactory modelFactory;

	@Before
	public void init() throws Exception{
		emf = AbstractTestSuite.getEmf();
		em = emf.createEntityManager();
		jdbcConnection = new JdbcConnection();
		modelFactory = new ModelFactory();
		
		setUpDatabase();
	}

	@After
	public void clean() throws Exception {
		em.close();
		JdbcHelper.cleanTables(jdbcConnection);
		jdbcConnection.disconnect();
	}

	protected void setUpDatabase() throws Exception {
	}

}
