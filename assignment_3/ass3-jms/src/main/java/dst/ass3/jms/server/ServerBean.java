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

import dst.ass3.dto.ProcessTaskDTO;
import dst.ass3.dto.RateTaskDTO;
import dst.ass3.dto.TaskDTO;
import dst.ass3.jms.Names;
import dst.ass3.model.ITask;
import dst.ass3.model.Task;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

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

            connection.start();
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
            System.out.printf("Received message: {%d, %s}%n", type, m.getObject());
            switch (type) {
            case Names.MSG_SCHED_CREATE:
                handleSchedCreate(m.getObject());
                break;
            case Names.MSG_SCHED_INFO:
                handleSchedInfo(m.getObject());
                break;
            case Names.MSG_CLUSTER_DECISION:
                handleClusterDecision(m.getObject());
                break;
            case Names.MSG_COMP_PROCESSED:
                handleCompProcessed(m.getObject());
                break;
            default:
                System.err.printf("Invalid message type received: %d%n", type);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void handleCompProcessed(Serializable object) {
        final ProcessTaskDTO dto = (ProcessTaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        try {
            final ITask task = getTask(dto.getId());

            /* Persist to DB. */

            task.setStatus(dto.getStatus());

            entityManager.persist(task);

            /* Notify scheduler. */

            final TaskDTO schedDTO = new TaskDTO(task);
            final ObjectMessage sm = session.createObjectMessage(schedDTO);
            sm.setIntProperty(Names.PROP_TYPE, Names.MSG_SRV_PROCESSED);
            schedulerProducer.send(sm);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace(); /* getSingleResult() throws a wide variety of exceptions. */
        }
    }

    private void handleClusterDecision(Serializable object) {
        final RateTaskDTO dto = (RateTaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        try {
            final ITask task = getTask(dto.getId());

            /* Persist to DB. */

            task.setComplexity(dto.getComplexity());
            task.setStatus(dto.getStatus());
            task.setRatedBy(dto.getRatedBy());

            entityManager.persist(task);

            if (dto.getStatus() == TaskStatus.READY_FOR_PROCESSING) {
                /* Forward to computers. */

                final ProcessTaskDTO ptDTO = new ProcessTaskDTO(task);
                final ObjectMessage sm = session.createObjectMessage(ptDTO);
                sm.setIntProperty(Names.PROP_TYPE, Names.MSG_SRV_ASSIGN);
                sm.setStringProperty(Names.PROP_CLUSTER, dto.getRatedBy());
                sm.setStringProperty(Names.PROP_COMPLEXITY, dto.getComplexity().toString());
                computerProducer.send(sm);
            } else {
                /* Notify scheduler. */

                final TaskDTO schedDTO = new TaskDTO(task);
                final ObjectMessage sm = session.createObjectMessage(schedDTO);
                sm.setIntProperty(Names.PROP_TYPE, Names.MSG_SRV_DENIED);
                schedulerProducer.send(sm);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace(); /* getSingleResult() throws a wide variety of exceptions. */
        }
    }

    private void handleSchedInfo(Serializable object) {
        final TaskDTO dto = (TaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        final ITask task = getTask(dto.getId());

        try {
            /* Reply to scheduler. */

            final TaskDTO schedDTO = new TaskDTO(task);
            final ObjectMessage sm = session.createObjectMessage(schedDTO);
            sm.setIntProperty(Names.PROP_TYPE, Names.MSG_SRV_INFO);
            schedulerProducer.send(sm);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace(); /* getSingleResult() throws a wide variety of exceptions. */
        }
    }

    private ITask getTask(long id) {
        return entityManager.createQuery("from Task where id = :id", ITask.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    private void handleSchedCreate(Serializable o) {
        final TaskDTO dto = (TaskDTO)o;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        /* Persist the task. */

        final Task task = new Task();
        task.setJobId(dto.getJobId());
        task.setStatus(TaskStatus.ASSIGNED);
        task.setComplexity(TaskComplexity.UNRATED);

        entityManager.persist(task);

        try {
            /* Forward to a cluster. */

            final RateTaskDTO clusterDTO = new RateTaskDTO(task);
            final ObjectMessage cm = session.createObjectMessage(clusterDTO);
            cm.setIntProperty(Names.PROP_TYPE, Names.MSG_SRV_DECIDE);
            clusterProducer.send(cm);

            /* Reply to scheduler. */

            final TaskDTO schedDTO = new TaskDTO(task);
            final ObjectMessage sm = session.createObjectMessage(schedDTO);
            sm.setIntProperty(Names.PROP_TYPE, Names.MSG_SRV_CREATED);
            schedulerProducer.send(sm);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
