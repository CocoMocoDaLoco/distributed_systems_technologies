package dst.ass1.jpa;

import static junit.framework.Assert.assertEquals;

import javax.persistence.Query;

import org.junit.Test;

import dst.ass1.AbstractTest;
import dst.ass1.jpa.interceptor.SQLInterceptor;

public class Test_4d extends AbstractTest {
	
	@Test
	public void testInterceptor() {
		SQLInterceptor.resetCounter();
		
		Query c = em.createQuery("select c from Computer c");
		c.getResultList();
		
		assertEquals(1, SQLInterceptor.getSelectCount());
		
		c = em.createQuery("select distinct c from Computer c");
		c.getResultList();
		
		assertEquals(2, SQLInterceptor.getSelectCount());
		
		c = em.createQuery("select e from Execution e");
		c.getResultList();
		assertEquals(3, SQLInterceptor.getSelectCount());
	}
	
}
