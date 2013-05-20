package dst.ass3.jms.scheduler.impl;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dst.ass3.jms.Names;
import dst.ass3.jms.scheduler.IScheduler;

public class Scheduler implements IScheduler {

    private ISchedulerListener listener;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    @Override
    public void start() {
        try {
            InitialContext ctx = new InitialContext();
            ConnectionFactory connectionFactory = (ConnectionFactory)
                    ctx.lookup(Names.CONNECTION_FACTORY);
            Queue queue = (Queue)ctx.lookup(Names.SERVER_QUEUE);

            connection = connectionFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try { messageProducer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { session.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { connection.close(); } catch (JMSException e) { e.printStackTrace(); }
    }

    @Override
    public void assign(long jobId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void info(long taskId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSchedulerListener(ISchedulerListener listener) {
        this.listener = listener;
    }

}
