package dst.ass1.nosql.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.dao.IJobDAO;
import dst.ass1.jpa.model.IJob;
import dst.ass1.nosql.IMongoDbDataLoader;
import dst.ass1.nosql.MongoTestData;

public class MongoDbDataLoader implements IMongoDbDataLoader {
    private static final String DATABASE_NAME = "dst";
    public static final String COLLECTION_NAME = "JobResult";

    private final EntityManager entityManager;

    public MongoDbDataLoader(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public void loadData() throws Exception {
        Session session = entityManager.unwrap(Session.class);
        IJobDAO jobDAO = new DAOFactory(session).getJobDAO();
        List<IJob> jobs = jobDAO.findJobForStatusFinishedStartandFinish(null, null);

        Mongo client = new Mongo();
        DB db = client.getDB(DATABASE_NAME);
        DBCollection collection = db.getCollection(COLLECTION_NAME);

        MongoTestData testData = new MongoTestData();

        collection.createIndex(new BasicDBObject("job_id", 1));

        int i = 0;
        for (IJob job : jobs) {
            String jsonString = String.format(
                    "{ " +
                    "  \"job_id\" : %d, " +
                    "  \"last_updated\" : %d, " +
                    "  \"%s\" : %s " +
                    "}",
                    job.getId(),
                    job.getExecution().getEnd().getTime(), /* Weird, but that's what the test expects. */
                    testData.getDataDesc(job.getId().intValue()),
                    testData.getStringData(job.getId().intValue()));

            DBObject o = (DBObject)JSON.parse(jsonString);
            collection.insert(o);
        }

        client.close();
    }

}
