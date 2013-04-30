package dst.ass1;

import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import dst.ass1.jpa.dao.DAOFactory;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.JobStatus;
import dst.ass1.nosql.IMongoDbDataLoader;
import dst.ass1.nosql.IMongoDbQuery;
import dst.ass1.nosql.MongoDbFactory;
import dst.ass1.nosql.TestData;

public abstract class AbstractNoSQLTest {

	private EntityManagerFactory emf;
	private DAOFactory daoFactory;
	
	protected MongoDbFactory mongoDbFactory;
	protected EntityManager em;
	protected IMongoDbDataLoader mongoDbDataLoader;
	protected IMongoDbQuery mongoDbQuery;

	@Before
	public void setUp() throws Exception {
		emf = Persistence.createEntityManagerFactory("dst_pu");
		em = emf.createEntityManager();
		daoFactory = new DAOFactory((Session) em.getDelegate());

		(new TestData(em)).insertTestData();

		Mongo mongo = new Mongo();
		DB db = mongo.getDB("dst");
		DBCollection collection = db.getCollection("JobResult");
		collection.drop();
		mongo.close();

		mongoDbFactory = new MongoDbFactory();
		mongoDbDataLoader = mongoDbFactory.createDataLoader(em);

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		List<IJob> jobs = daoFactory.getJobDAO().findAll();
		for (IJob j : jobs) {
			j.getExecution().setEnd(new Date());
			Thread.sleep(1000);
			j.getExecution().setStatus(JobStatus.FINISHED);
			em.persist(j);
		}

		tx.commit();
		
		mongoDbDataLoader.loadData();
	}

	@After
	public void tearDown() throws SQLException, UnknownHostException,
			MongoException {
		em.close();
		emf.close();

		Mongo mongo = new Mongo();
		DB db = mongo.getDB("dst");
		DBCollection collection = db.getCollection("JobResult");
		collection.drop();
		mongo.close();
	}

}