package dst.ass1.nosql;

import java.util.ArrayList;
import java.util.List;


import dst.ass1.AbstractNoSQLTest;
import dst.ass1.jpa.model.IJob;

import org.hibernate.Session;
import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import dst.ass1.jpa.dao.DAOFactory;

import static org.junit.Assert.*;

public class Test_5a extends AbstractNoSQLTest {
	
	@Test
	public void testMongoDataLoader() throws Exception {
		MongoTestData mongoTestData = new MongoTestData();
		Mongo mongo = new Mongo();
		DB db = mongo.getDB("dst");
		DBCollection collection = db.getCollection("JobResult");
		DBCursor cursor = collection.find();
		ArrayList<DBObject> dbData = new ArrayList<DBObject>(); 
		while (cursor.hasNext()) {
			dbData.add(cursor.next());
		}
		cursor.close();
		
		DAOFactory daoFactory = new DAOFactory((Session)em.getDelegate());
		List<IJob> jobs = daoFactory.getJobDAO().findAll();
		
		assertEquals(jobs.size(), dbData.size());
		
		for (IJob job : jobs) {
			boolean isFound = false;
			for (DBObject obj :  dbData) {
				if (Long.valueOf(obj.get("job_id").toString()) == job.getId()) {
					isFound = true;
					assertEquals(job.getExecution().getEnd().getTime(), Long.valueOf(obj.get("last_updated").toString()).longValue());
					String desc = mongoTestData.getDataDesc(job.getId().intValue());					
					assertEquals(obj.get(desc), JSON.parse(mongoTestData.getStringData(job.getId().intValue())));
				}
			}
			assertTrue(isFound);
		}
	}
	
}
