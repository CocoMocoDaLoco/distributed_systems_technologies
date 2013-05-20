package dst.ass3.jms.cluster.impl;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dst.ass3.jms.Names;
import dst.ass3.jms.cluster.ICluster;

public class Cluster implements ICluster {

    private final String name;
    private IClusterListener listener;

    private Connection connection;
    private Session session;
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
            Queue qin = (Queue)ctx.lookup(Names.CLUSTER_QUEUE);

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(qout);

            messageConsumer = session.createConsumer(qin);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try { messageConsumer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { messageProducer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { connection.close(); } catch (JMSException e) { e.printStackTrace(); }
    }

    @Override
    public void setClusterListener(IClusterListener listener) {
        this.listener = listener;
    }

}
