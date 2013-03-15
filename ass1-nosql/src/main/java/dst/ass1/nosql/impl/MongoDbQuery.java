package dst.ass1.nosql.impl;

import java.util.List;

import com.mongodb.DB;
import com.mongodb.DBObject;

import dst.ass1.nosql.IMongoDbQuery;

public class MongoDbQuery implements IMongoDbQuery {

    private final DB db;

    public MongoDbQuery(DB db) {
        this.db = db;
    }

    @Override
    public Long findLastUpdatedByJobId(Long jobId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Long> findLastUpdatedGt(Long time) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<DBObject> mapReduceWorkflow() {
        // TODO Auto-generated method stub
        return null;
    }

}
