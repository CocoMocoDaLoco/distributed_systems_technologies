package dst.ass1.nosql;

import static org.junit.Assert.assertEquals;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.Session;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import dst.ass1.AbstractNoSQLTest;
import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IJob;

public class Test_5b extends AbstractNoSQLTest {

	@Test
	public void testFindLastUpdatedQuery() throws UnknownHostException,
			MongoException {
		Mongo mongo = new Mongo();
		DB db = mongo.getDB("dst");
		
		IMongoDbQuery mongoQuery = mongoDbFactory.createQuery(db);
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());

		List<IJob> jobs = daoFactory.getJobDAO().findAll();
		for (IJob job : jobs) {
			assertEquals(job.getExecution().getEnd().getTime(), mongoQuery
					.findLastUpdatedByJobId(job.getId()).longValue());
		}

		mongo.close();

	}

	@Test
	public void testLastUpdatedGt() throws UnknownHostException, MongoException {
		long time = 1325397600;
		DAOFactory daoFactory = new DAOFactory((Session) em.getDelegate());
		Mongo mongo = new Mongo();
		DB db = mongo.getDB("dst");
		IMongoDbQuery mongoQuery = mongoDbFactory.createQuery(db);

		List<IJob> jobs = daoFactory.getJobDAO().findAll();
		ArrayList<Long> jobIds1 = new ArrayList<Long>();
		for (IJob job : jobs) {
			if (job.getExecution().getEnd().getTime() > time)
				jobIds1.add(job.getId());
		}

		List<Long> jobIds2 = mongoQuery.findLastUpdatedGt(time);

		assertEquals(jobIds2.size(), jobIds1.size());

		Collections.sort(jobIds1);
		Collections.sort(jobIds2);

		for (int i = 0; i < jobIds1.size(); i++) {
			assertEquals(jobIds1.get(i), jobIds2.get(i));
		}

	}
}
