package dst.ass1.jpa;

import static org.junit.Assert.assertFalse;

import java.sql.SQLException;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.model.IAddress;
import dst.ass1.jpa.util.JdbcHelper;

public class Test_1a06 extends AbstractTest {
		
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void testAddressEmbedded() {
		IAddress address = modelFactory.createAddress();
		address.setCity("city1");
		address.setStreet("street1");
		address.setZipCode("zip1");
		
		em.persist(em);

	}
	
	@Test
	public void testAddressEmbeddedJdbc() throws ClassNotFoundException, SQLException {
		assertFalse(JdbcHelper.isTable("address", jdbcConnection));
	}
}
