package dst.ass3.jms.server;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import dst.ass3.dto.TaskDTO;
import dst.ass3.jms.Names;

@MessageDriven(mappedName = Names.SERVER_QUEUE)
public class ServerBean implements MessageListener {

    @Resource(mappedName = Names.CONNECTION_FACTORY)
    private ConnectionFactory connectionFactory;
    private Connection connection = null;
    private Session session = null;

    @Resource(mappedName = Names.CLUSTER_QUEUE)
    private Queue clusterQueue;
    private MessageProducer clusterProducer = null;

    @Resource(mappedName = Names.SCHEDULER_QUEUE)
    private Queue schedulerQueue;
    private MessageProducer schedulerProducer = null;

    @Resource(mappedName = Names.COMPUTER_TOPIC)
    private Topic computerTopic;
    private MessageProducer computerProducer = null;

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void start() {
        try {
            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            clusterProducer = session.createProducer(clusterQueue);
            schedulerProducer = session.createProducer(schedulerQueue);
            computerProducer = session.createProducer(computerTopic);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void stop() {
        try { if (clusterProducer != null) session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (schedulerProducer != null) session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (computerProducer != null) session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (session != null) session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (connection != null) connection.close(); } catch (JMSException e) { e.printStackTrace(); }
    }

    @Override
    public void onMessage(Message msg) {
        ObjectMessage m = (ObjectMessage)msg;
        if (m == null) {
            System.err.printf("Invalid message received: %s%n", msg);
            return;
        }

        try {
            final int type = m.getIntProperty(Names.PROP_TYPE);
            switch (type) {
            case Names.MSG_SCHED_CREATE:
                handleSchedCreate(m.getObject());
                break;
            default:
                System.err.printf("Invalid message type received: %d%n", type);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void handleSchedCreate(Serializable o) {
        TaskDTO dto = (TaskDTO)o;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        System.out.println("Handling SCHED_CREATE");
    }

}
