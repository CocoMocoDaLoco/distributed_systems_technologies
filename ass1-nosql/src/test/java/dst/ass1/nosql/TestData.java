package dst.ass1.nosql;

import java.math.BigDecimal;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.mongodb.DB;
import com.mongodb.Mongo;

import de.flapdoodle.embedmongo.MongoDBRuntime;
import de.flapdoodle.embedmongo.MongodExecutable;
import de.flapdoodle.embedmongo.MongodProcess;
import de.flapdoodle.embedmongo.config.MongodConfig;
import de.flapdoodle.embedmongo.distribution.Version;
import dst.ass1.jpa.model.IAddress;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.ICluster;
import dst.ass1.jpa.model.IComputer;
import dst.ass1.jpa.model.IEnvironment;
import dst.ass1.jpa.model.IExecution;
import dst.ass1.jpa.model.IGrid;
import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.IMembershipKey;
import dst.ass1.jpa.model.IPerson;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.JobStatus;
import dst.ass1.jpa.model.ModelFactory;

public class TestData {

	@SuppressWarnings("unused")
	private static DB db;
	@SuppressWarnings("unused")
	private static MongodProcess mongoProcess;
	private static MongodExecutable mongodExecutable;

	static {
		try {
			// Get open port
			final ServerSocket socket = new ServerSocket(27017);
			final int port = socket.getLocalPort();
			socket.close();

			// create runtime
			final MongoDBRuntime runtime = MongoDBRuntime.getDefaultInstance();
			mongodExecutable = runtime.prepare(new MongodConfig(Version.V2_1_2,
					port, false));
			mongoProcess = mongodExecutable.start();

			// create database
			final Mongo mongo = new Mongo("localhost", port);
			db = mongo.getDB(UUID.randomUUID().toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private EntityManager em;

	public TestData(EntityManager em) {
		this.em = em;
	}

	public void insertTestData() throws NoSuchAlgorithmException {

		ModelFactory modelFactory = new ModelFactory();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		// Addresses
		IAddress address1 = modelFactory.createAddress();
		IAddress address2 = modelFactory.createAddress();
		IAddress address3 = modelFactory.createAddress();
		IAddress address4 = modelFactory.createAddress();

		address1.setCity("city1");
		address1.setStreet("street1");
		address1.setZipCode("zip1");

		address2.setCity("city2");
		address2.setStreet("street2");
		address2.setZipCode("zip2");

		address3.setCity("city3");
		address3.setStreet("street3");
		address3.setZipCode("zip3");

		address4.setCity("city4");
		address4.setStreet("street4");
		address4.setZipCode("zip4");

		// Admins
		IAdmin admin1 = modelFactory.createAdmin();
		IAdmin admin2 = modelFactory.createAdmin();

		((IPerson) admin1).setFirstName("admin1");
		((IPerson) admin1).setLastName("admin1");
		((IPerson) admin1).setAddress(address1);

		((IPerson) admin2).setFirstName("admin2");
		((IPerson) admin2).setLastName("admin2");
		((IPerson) admin2).setAddress(address2);

		// Users
		MessageDigest md = MessageDigest.getInstance("MD5");

		IUser user1 = modelFactory.createUser();
		((IPerson) user1).setFirstName("user1");
		((IPerson) user1).setLastName("user1");
		((IPerson) user1).setAddress(address3);
		user1.setAccountNo("account1");
		user1.setUsername("user1");
		user1.setBankCode("bank1");
		user1.setPassword(md.digest("pw1".getBytes()));

		IUser user2 = modelFactory.createUser();
		((IPerson) user2).setFirstName("user2");
		((IPerson) user2).setLastName("user2");
		((IPerson) user2).setAddress(address4);
		user2.setAccountNo("account2");
		user2.setUsername("user2");
		user2.setBankCode("bank2");
		user2.setPassword(md.digest("pw2".getBytes()));

		em.persist(admin1);
		em.persist(admin2);
		em.persist(user1);
		em.persist(user2);

		// Computers
		IComputer computer1 = modelFactory.createComputer();
		computer1.setName("computer1");
		computer1.setCpus(2);
		computer1.setLocation("AUT-VIE-location1");
		computer1.setCreation(new Date(0));
		computer1.setLastUpdate(new Date(0));

		IComputer computer2 = modelFactory.createComputer();
		computer2.setName("computer2");
		computer2.setCpus(3);
		computer2.setLocation("AUT-VIE-location2");
		computer2.setCreation(new Date(0));
		computer2.setLastUpdate(new Date(0));

		IComputer computer3 = modelFactory.createComputer();
		computer3.setName("computer3");
		computer3.setCpus(4);
		computer3.setLocation("AUT-VIE-location3");
		computer3.setCreation(new Date(0));
		computer3.setLastUpdate(new Date(0));

		// Clusters
		ICluster cluster1 = modelFactory.createCluster();
		cluster1.setAdmin(admin1);
		cluster1.setName("cluster1");
		cluster1.setLastService(new Date());
		cluster1.setNextService(new Date());

		ICluster cluster2 = modelFactory.createCluster();
		cluster2.setAdmin(admin2);
		cluster2.setName("cluster2");
		cluster2.setLastService(new Date());
		cluster2.setNextService(new Date());

		cluster1.addComposedOf(cluster2);
		cluster2.addPartOf(cluster1);

		admin1.addCluster(cluster1);
		admin2.addCluster(cluster2);

		cluster1.addComputer(computer1);
		cluster1.addComputer(computer2);
		computer1.setCluster(cluster1);
		computer2.setCluster(cluster1);

		cluster2.addComputer(computer3);
		computer3.setCluster(cluster2);

		// Schedulers

		// Grids

		IGrid grid = modelFactory.createGrid();
		grid.setName("grid1");
		grid.setLocation("vienna");
		grid.setCostsPerCPUMinute(new BigDecimal(20));

		grid.addCluster(cluster1);
		grid.addCluster(cluster2);

		cluster1.setGrid(grid);
		cluster2.setGrid(grid);

		em.persist(grid);

		// Memberships
		IMembership membership1 = modelFactory.createMembership();
		membership1.setDiscount(0.10);
		membership1.setRegistration(new Date());

		IMembershipKey key1 = modelFactory.createMembershipKey();
		key1.setUser(user1);
		key1.setGrid(grid);

		membership1.setId(key1);
		user1.addMembership(membership1);

		IMembership membership2 = modelFactory.createMembership();
		membership2.setDiscount(0.10);
		membership2.setRegistration(new Date());

		IMembershipKey key2 = modelFactory.createMembershipKey();
		key2.setUser(user2);
		key2.setGrid(grid);

		membership2.setId(key2);
		user2.addMembership(membership2);

		// Environments
		IEnvironment environment1 = modelFactory.createEnvironment();
		environment1.setWorkflow("workflow1");
		environment1.addParam("param1");
		environment1.addParam("param2");
		environment1.addParam("param3");

		IEnvironment environment2 = modelFactory.createEnvironment();
		environment2.setWorkflow("workflow2");
		environment2.addParam("param4");

		IEnvironment environment3 = modelFactory.createEnvironment();
		environment2.setWorkflow("workflow3");
		environment3.addParam("param5");

		em.persist(environment1);
		em.persist(environment2);
		em.persist(environment3);

		IExecution ex1 = modelFactory.createExecution();
		ex1.setStart(new Date(System.currentTimeMillis() - 36000000));
		ex1.setEnd(new Date());
		ex1.setStatus(JobStatus.SCHEDULED);

		IExecution ex2 = modelFactory.createExecution();
		ex2.setStart(new Date());
		ex2.setStatus(JobStatus.SCHEDULED);

		IExecution ex3 = modelFactory.createExecution();
		ex3.setStart(new Date());
		ex3.setStatus(JobStatus.SCHEDULED);

		ex1.addComputer(computer1);
		ex2.addComputer(computer2);
		ex3.addComputer(computer3);

		computer1.addExecution(ex1);
		computer2.addExecution(ex2);
		computer3.addExecution(ex3);

		// Jobs
		IJob job1 = modelFactory.createJob();
		job1.setNumCPUs(2);
		job1.setExecutionTime(0);
		job1.setEnvironment(environment1);
		job1.setExecution(ex1);
		job1.setUser(user1);
		user1.addJob(job1);

		IJob job2 = modelFactory.createJob();
		job2.setNumCPUs(3);
		job2.setExecutionTime(0);
		job2.setEnvironment(environment2);
		job2.setExecution(ex2);
		job2.setUser(user2);
		user2.addJob(job2);

		IJob job3 = modelFactory.createJob();
		job3.setNumCPUs(4);
		job3.setExecutionTime(0);
		job3.setEnvironment(environment3);
		job3.setExecution(ex3);
		job3.setUser(user1);
		user1.addJob(job3);

		ex1.setJob(job1);
		ex2.setJob(job2);
		ex3.setJob(job3);

		em.persist(admin1);
		em.persist(admin2);
		em.persist(user1);
		em.persist(user2);
		em.persist(cluster1);
		em.persist(cluster2);
		em.persist(computer1);
		em.persist(computer2);
		em.persist(computer3);
		em.persist(grid);
		em.persist(membership1);
		em.persist(membership2);
		em.persist(job1);
		em.persist(job2);
		em.persist(job3);

		tx.commit();

	}

}
