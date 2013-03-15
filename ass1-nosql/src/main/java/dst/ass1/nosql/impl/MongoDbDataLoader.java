package dst.ass1.nosql.impl;

import javax.persistence.EntityManager;

import dst.ass1.nosql.IMongoDbDataLoader;

public class MongoDbDataLoader implements IMongoDbDataLoader {

    private final EntityManager entityManager;

    public MongoDbDataLoader(EntityManager em) {
        this.entityManager = em;
    }

    @Override
    public void loadData() throws Exception {
        // TODO Auto-generated method stub
    }

}
