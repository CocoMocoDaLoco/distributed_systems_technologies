package dst.ass3.jms.cluster.impl;

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

import dst.ass3.dto.RateTaskDTO;
import dst.ass3.jms.Names;
import dst.ass3.jms.cluster.ICluster;
import dst.ass3.jms.cluster.ICluster.IClusterListener.TaskDecideResponse;
import dst.ass3.jms.cluster.ICluster.IClusterListener.TaskResponse;
import dst.ass3.model.TaskStatus;

public class Cluster implements ICluster {

    private final String name;
    private IClusterListener listener;

    private Connection connection;
    private Session session;
    private Queue clusterQueue;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;

    public Cluster(String name) {
        this.name = name;
    }

    @Override
    public void start() {
        try {
            InitialContext ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)
                    ctx.lookup(Names.CONNECTION_FACTORY);
            Queue qout = (Queue)ctx.lookup(Names.SERVER_QUEUE);
            clusterQueue = (Queue)ctx.lookup(Names.CLUSTER_QUEUE);

            connection = connectionFactory.createConnection();
            connection.setClientID(name);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(qout);

            /* TODO: How to disconnect from queue? */

            messageConsumer = session.createConsumer(clusterQueue);
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    Cluster.this.onMessage(message);
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
            case Names.MSG_SRV_DECIDE:
                handleSrvDecide(m.getObject());
                break;
            default:
                System.err.printf("Invalid message type received: %d%n", type);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void handleSrvDecide(Serializable object) {
        final RateTaskDTO dto = (RateTaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        if (listener == null) {
            return;
        }

        try {
            TaskDecideResponse decision = listener.decideTask(dto, name);

            dto.setRatedBy(name);
            if (decision.resp == TaskResponse.ACCEPT) {
                dto.setComplexity(decision.complexity);
                dto.setStatus(TaskStatus.READY_FOR_PROCESSING);
            } else {
                dto.setStatus(TaskStatus.PROCESSING_NOT_POSSIBLE);
            }

            ObjectMessage msg = session.createObjectMessage(dto);
            msg.setIntProperty(Names.PROP_TYPE, Names.MSG_CLUSTER_DECISION);
            messageProducer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try { if (messageConsumer != null) messageConsumer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (messageProducer != null) messageProducer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (session != null) session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (connection != null) connection.close(); } catch (JMSException e) { e.printStackTrace(); }
    }

    @Override
    public void setClusterListener(IClusterListener listener) {
        this.listener = listener;
    }

}
