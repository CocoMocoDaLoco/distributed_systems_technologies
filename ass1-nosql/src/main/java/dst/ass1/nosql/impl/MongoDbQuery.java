package dst.ass1.nosql.impl;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;

import dst.ass1.nosql.IMongoDbQuery;

public class MongoDbQuery implements IMongoDbQuery {

    private final DBCollection collection;

    public MongoDbQuery(DB db) {
        collection = db.getCollection(MongoDbDataLoader.COLLECTION_NAME);
    }

    @Override
    public Long findLastUpdatedByJobId(Long jobId) {
        BasicDBObject query = new BasicDBObject("job_id", jobId);

        DBCursor cursor = collection.find(query);

        if (!cursor.hasNext()) {
            System.out.printf("findLastUpdatedByJobId: No matching entry found for job_id: %d%n", jobId);
            return new Long(-1);
        }

        DBObject object = cursor.next();
        Long lastUpdated = (Long)object.get("last_updated");

        System.out.printf("findLastUpdatedByJobId: Found last_updated: %d for job_id: %d%n",
                lastUpdated.longValue(), jobId);

        return lastUpdated.longValue();
    }

    @Override
    public List<Long> findLastUpdatedGt(Long time) {
        BasicDBObject query = new BasicDBObject("last_updated",
                new BasicDBObject("$gt", time));
        BasicDBObject filter = new BasicDBObject("job_id", 1);

        DBCursor cursor = collection.find(query, filter);

        List<Long> l = new ArrayList<Long>();

        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            Integer jobId = (Integer)object.get("job_id");
            l.add(new Long(jobId.longValue()));

            System.out.printf("findLastUpdatedGt: Found %s%n", object);
        }

        return l;
    }

    @Override
    public List<DBObject> mapReduceWorkflow() {
        String m =
                "function() { " +
                "    for (var prop in this) { " +
                "        switch (prop) { " +
                "        case \"_id\": " +
                "        case \"job_id\": " +
                "        case \"last_updated\": " +
                "            continue; " +
                "        default: "  +
                "            emit (prop, 1); " +
                "        } " +
                "    }" +
                "}";
        String r = "function(key, values) { " +
                "    var sum = 0; " +
                "    for (var v in values) { " +
                "        sum += v; " +
                "    } " +
                "    return sum; " +
                "}";

        MapReduceOutput out = collection.mapReduce(m, r, null,
                MapReduceCommand.OutputType.INLINE, null);

        List<DBObject> l = new ArrayList<DBObject>();
        for (DBObject object : out.results()) {
            l.add(object);
            System.out.printf("mapReduceWorkflow: %s%n", object);
        }

        return l;
    }

}
