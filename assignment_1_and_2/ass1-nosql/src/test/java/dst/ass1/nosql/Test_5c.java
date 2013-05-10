package dst.ass1.nosql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.UnknownHostException;
import java.util.List;

import org.junit.Test;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import dst.ass1.AbstractNoSQLTest;

public class Test_5c extends AbstractNoSQLTest {

	@Test
	public void testMapReduceWorkflow() throws UnknownHostException,
			MongoException {
		Mongo mongo = new Mongo();
		DB db = mongo.getDB("dst");
		IMongoDbQuery mongoQuery = mongoDbFactory.createQuery(db);

		List<DBObject> dbObjects = mongoQuery.mapReduceWorkflow();
		boolean a[] = new boolean[dbObjects.size()];
		assertEquals(3, dbObjects.size());
		for (int i = 0; i < dbObjects.size(); i++) {
			DBObject obj = dbObjects.get(i);

			if (obj.get("_id") != null
					&& obj.get("_id").equals("alignment_block")) {
				assertTrue(Double.valueOf(obj.get("value").toString())
						.compareTo(new Double(1)) == 0);
				a[i] = true;
			} else if (obj.get("_id") != null && obj.get("_id").equals("logs")) {
				assertTrue(Double.valueOf(obj.get("value").toString())
						.compareTo(new Double(1)) == 0);
				a[i] = true;
			} else if (obj.get("_id") != null
					&& obj.get("_id").equals("result_matrix")) {
				assertTrue(Double.valueOf(obj.get("value").toString())
						.compareTo(new Double(1)) == 0);
				a[i] = true;
			}
		}

		for (int i = 0; i < a.length; i++)
			assertTrue(a[i]);
	}

}
