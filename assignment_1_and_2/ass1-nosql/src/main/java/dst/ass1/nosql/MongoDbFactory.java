package dst.ass1.nosql;

import javax.persistence.EntityManager;

import com.mongodb.DB;

import dst.ass1.nosql.impl.MongoDbDataLoader;
import dst.ass1.nosql.impl.MongoDbQuery;

public class MongoDbFactory {

    public IMongoDbDataLoader createDataLoader(EntityManager em) {
        return new MongoDbDataLoader(em);
    }

    public IMongoDbQuery createQuery(DB db) {
        return new MongoDbQuery(db);
    }

}
