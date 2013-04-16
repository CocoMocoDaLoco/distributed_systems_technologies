package dst.ass2.ejb.session;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

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
import dst.ass2.ejb.session.interfaces.ITestingBean;

public class TestingBean implements ITestingBean {

    private EntityManager em;

    @Override
    public void insertTestData() {
        
        ModelFactory modelFactory = new ModelFactory();

        System.out.println("Started");

        IAddress address1 = modelFactory.createAddress();
        IAddress address2 = modelFactory.createAddress();
        IAddress address3 = modelFactory.createAddress();

        address1.setCity("city1");
        address1.setStreet("street1");
        address1.setZipCode("1140");

        address2.setCity("city2");
        address2.setStreet("street2");
        address2.setZipCode("1150");

        address3.setCity("city3");
        address3.setStreet("street3");
        address3.setZipCode("1160");

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        IUser user1 = modelFactory.createUser();
        user1.setAccountNo("111111");
        user1.setBankCode("1111");
        ((IPerson) user1).setFirstName("Hans");
        ((IPerson) user1).setLastName("Mueller");
        ((IPerson) user1).setAddress(address1);
        user1.setUsername("hansi");
        user1.setPassword(md.digest("pw".getBytes()));

        IUser user2 = modelFactory.createUser();
        user2.setAccountNo("222222");
        user2.setBankCode("1111");
        ((IPerson) user2).setFirstName("Franz");
        ((IPerson) user2).setLastName("Mueller");
        ((IPerson) user2).setAddress(address2);
        user2.setUsername("franz");
        user2.setPassword(md.digest("liebe".getBytes()));

        IAdmin admin = modelFactory.createAdmin();
        ((IPerson) admin).setFirstName("Sepp");
        ((IPerson) admin).setLastName("Huber");
        ((IPerson) admin).setAddress(address3);

        em.persist(user1);
        em.persist(user2);
        em.persist(admin);

        IGrid grid1 = modelFactory.createGrid();
        grid1.setName("grid1");
        grid1.setLocation("location1");
        grid1.setCostsPerCPUMinute(new BigDecimal(5));

        IGrid grid2 = modelFactory.createGrid();
        grid2.setName("grid2");
        grid2.setLocation("location2");
        grid2.setCostsPerCPUMinute(new BigDecimal(7));

        em.persist(grid1);
        em.persist(grid2);

        ICluster cluster1 = modelFactory.createCluster();
        cluster1.setName("cl1");
        cluster1.setLastService(new Date());
        cluster1.setNextService(new Date());
        cluster1.setAdmin(admin);
        cluster1.setGrid(grid1);

        ICluster cluster2 = modelFactory.createCluster();
        cluster2.setName("cl2");
        cluster2.setLastService(new Date());
        cluster2.setNextService(new Date());
        cluster2.setAdmin(admin);
        cluster2.setGrid(grid2);

        admin.addCluster(cluster1);
        admin.addCluster(cluster2);

        IComputer computer1 = modelFactory.createComputer();
        computer1.setName("longername1");
        computer1.setCpus(new Integer(4));
        computer1.setLocation("AUT-XYZ@5678");
        computer1.setCreation(createDate(2000, 10, 10));
        computer1.setLastUpdate(createDate(2000, 10, 11));
        computer1.setCluster(cluster1);

        IComputer computer2 = modelFactory.createComputer();
        computer2.setName("longername2");
        computer2.setCpus(new Integer(6));
        computer2.setLocation("AUT-XYZ@1234");
        computer2.setCreation(createDate(2000, 10, 12));
        computer2.setLastUpdate(createDate(2000, 10, 13));
        computer2.setCluster(cluster1);

        IComputer computer3 = modelFactory.createComputer();
        computer3.setName("longername3");
        computer3.setCpus(new Integer(8));
        computer3.setLocation("AUT-XYZ@1234");
        computer3.setCreation(createDate(2000, 10, 14));
        computer3.setLastUpdate(createDate(2000, 10, 15));
        computer3.setCluster(cluster1);

        cluster1.addComputer(computer1);
        cluster1.addComputer(computer2);
        cluster1.addComputer(computer3);

        IComputer computer4 = modelFactory.createComputer();
        computer4.setName("longername4");
        computer4.setCpus(new Integer(4));
        computer4.setLocation("AUT-XYZ@5678");
        computer4.setCreation(createDate(2000, 10, 16));
        computer4.setLastUpdate(createDate(2000, 10, 17));
        computer4.setCluster(cluster2);

        IComputer computer5 = modelFactory.createComputer();
        computer5.setName("longername5");
        computer5.setCpus(new Integer(8));
        computer5.setLocation("AUT-XYZ@5678");
        computer5.setCreation(createDate(2000, 10, 18));
        computer5.setLastUpdate(createDate(2000, 10, 19));
        computer5.setCluster(cluster2);

        cluster2.addComputer(computer4);
        cluster2.addComputer(computer5);

        em.persist(cluster1);
        em.persist(cluster2);

        em.persist(computer1);
        em.persist(computer2);
        em.persist(computer3);
        em.persist(computer4);
        em.persist(computer5);
        em.persist(admin);
        em.persist(cluster2);
        em.persist(grid1);
        em.persist(grid2);

        IMembership membership1 = modelFactory.createMembership();
        IMembershipKey keyId1 = modelFactory.createMembershipKey();
        keyId1.setGrid(grid1);
        keyId1.setUser(user1);
        membership1.setRegistration(createDate(2009, 1, 1));
        membership1.setDiscount(new Double(0.1));
        membership1.setId(keyId1);

        IMembership membership2 = modelFactory.createMembership();
        IMembershipKey keyId2 = modelFactory.createMembershipKey();
        keyId2.setGrid(grid1);
        keyId2.setUser(user2);
        membership2.setRegistration(createDate(2008, 2, 2));
        membership2.setDiscount(new Double(0.2));
        membership2.setId(keyId2);

        IMembership membership3 = modelFactory.createMembership();
        IMembershipKey keyId3 = modelFactory.createMembershipKey();
        keyId3.setGrid(grid2);
        keyId3.setUser(user1);
        membership3.setRegistration(createDate(2007, 3, 3));
        membership3.setDiscount(new Double(0.3));
        membership3.setId(keyId3);

        em.persist(membership1);
        em.persist(membership2);
        em.persist(membership3);

        IEnvironment env1 = modelFactory.createEnvironment();
        env1.setWorkflow("workflow1");
        env1.setParams(new ArrayList<String>());

        IEnvironment env2 = modelFactory.createEnvironment();
        env2.setWorkflow("workflow2");
        env2.setParams(new ArrayList<String>());

        em.persist(env1);
        em.persist(env2);

        IExecution ex1 = modelFactory.createExecution();
        ex1.setStart(new Date(System.currentTimeMillis() - 1800000));
        ex1.setStatus(JobStatus.SCHEDULED);

        IJob job1 = modelFactory.createJob();
        job1.setNumCPUs(new Integer(3));
        job1.setExecutionTime(new Integer(0));
        job1.setEnvironment(env1);
        job1.setUser(user1);
        job1.setExecution(ex1);

        ex1.setJob(job1);
        ex1.addComputer(computer3);
        computer3.addExecution(ex1);
        em.persist(job1);
        em.persist(computer3);

        System.out.println("Finished");

    }

    private Date createDate(int year, int month, int day) {

        String temp = year + "/" + month + "/" + day;
        Date date = null;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            date = formatter.parse(temp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;

    }

}
