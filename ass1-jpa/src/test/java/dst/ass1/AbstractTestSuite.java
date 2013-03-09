package dst.ass1;

import java.sql.SQLException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;

public abstract class AbstractTestSuite {

	private static EntityManagerFactory emf;

	@BeforeClass
	public static void setUpClass() {
		emf = Persistence.createEntityManagerFactory("dst_pu");
	}

	@AfterClass
	public static void tearDownClass() throws SQLException {
		emf.close();
	}

	public static EntityManagerFactory getEmf() {
		return emf;
	}

	public static void setEmf(EntityManagerFactory emf) {
		AbstractTestSuite.emf = emf;
	}

}