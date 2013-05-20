package dst.ass3.jms.computer.impl;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dst.ass3.jms.Names;
import dst.ass3.jms.computer.IComputer;
import dst.ass3.model.TaskComplexity;

/* JMS Message Selector: p839. */

public class Computer implements IComputer {

    private final TaskComplexity complexity;
    private final String cluster;
    private final String name;
    private IComputerListener listener;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;

    public Computer(String name, String cluster, TaskComplexity complexity) {
        this.name = name;
        this.cluster = cluster;
        this.complexity = complexity;
    }

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
    public void setComputerListener(IComputerListener listener) {
        this.listener = listener;
    }

}
