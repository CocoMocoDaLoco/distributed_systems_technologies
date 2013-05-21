package dst.ass3.jms.scheduler.impl;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dst.ass3.dto.TaskDTO;
import dst.ass3.jms.Names;
import dst.ass3.jms.scheduler.IScheduler;
import dst.ass3.jms.scheduler.IScheduler.ISchedulerListener.InfoType;

public class Scheduler implements IScheduler {

    private ISchedulerListener listener;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;

    @Override
    public void start() {
        try {
            InitialContext ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)
                    ctx.lookup(Names.CONNECTION_FACTORY);
            Queue qout = (Queue)ctx.lookup(Names.SERVER_QUEUE);
            Queue qin = (Queue)ctx.lookup(Names.SCHEDULER_QUEUE);

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(qout);

            messageConsumer = session.createConsumer(qin);
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    Scheduler.this.onMessage(message);
                }
            });

            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void onMessage(Message message) {
        ObjectMessage m = (ObjectMessage)message;
        if (m == null) {
            System.err.printf("Invalid message received: %s%n", message);
            return;
        }

        try {
            final int type = m.getIntProperty(Names.PROP_TYPE);
            switch (type) {
            case Names.MSG_SRV_CREATED:
                handleSrvCreated(m.getObject());
                break;
            case Names.MSG_SRV_INFO:
                handleSrvInfo(m.getObject());
                break;
            case Names.MSG_SRV_DENIED:
                handleSrvDenied(m.getObject());
                break;
            case Names.MSG_SRV_PROCESSED:
                handleSrvProcessed(m.getObject());
                break;
            default:
                System.err.printf("Invalid message type received: %d%n", type);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void handleSrvProcessed(Serializable object) {
        final TaskDTO dto = (TaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        if (listener == null) {
            return;
        }

        listener.notify(InfoType.PROCESSED, dto);
    }

    private void handleSrvDenied(Serializable object) {
        final TaskDTO dto = (TaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        if (listener == null) {
            return;
        }

        listener.notify(InfoType.DENIED, dto);
    }

    private void handleSrvInfo(Serializable object) {
        final TaskDTO dto = (TaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        if (listener == null) {
            return;
        }

        listener.notify(InfoType.INFO, dto);
    }

    private void handleSrvCreated(Serializable object) {
        final TaskDTO dto = (TaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        if (listener == null) {
            return;
        }

        listener.notify(InfoType.CREATED, dto);
    }

    @Override
    public void stop() {
        try { if (messageConsumer != null) messageConsumer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (messageProducer != null) messageProducer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (session != null) session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (connection != null) connection.close(); } catch (JMSException e) { e.printStackTrace(); }
    }

    @Override
    public void assign(long jobId) {
        TaskDTO dto = new TaskDTO();
        dto.setJobId(jobId);

        try {
            ObjectMessage msg = session.createObjectMessage(dto);
            msg.setIntProperty(Names.PROP_TYPE, Names.MSG_SCHED_CREATE);
            messageProducer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void info(long taskId) {
        TaskDTO dto = new TaskDTO();
        dto.setId(taskId);

        try {
            ObjectMessage msg = session.createObjectMessage(dto);
            msg.setIntProperty(Names.PROP_TYPE, Names.MSG_SCHED_INFO);
            messageProducer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSchedulerListener(ISchedulerListener listener) {
        this.listener = listener;
    }

}
