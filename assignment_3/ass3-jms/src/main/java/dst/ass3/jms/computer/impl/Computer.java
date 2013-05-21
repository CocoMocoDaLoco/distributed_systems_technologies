package dst.ass3.jms.computer.impl;

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
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import dst.ass3.dto.ProcessTaskDTO;
import dst.ass3.jms.Names;
import dst.ass3.jms.computer.IComputer;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

/* JMS Message Selector: p839. */
/* PERSISTENT, p847. */

public class Computer implements IComputer {

    private final TaskComplexity complexity;
    private final String cluster;
    private final String name;
    private IComputerListener listener;

    private Connection connection;
    private Session session;
    private MessageProducer messageProducer;
    private MessageConsumer messageConsumer;

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
            Topic topic = (Topic)ctx.lookup(Names.COMPUTER_TOPIC);

            connection = connectionFactory.createConnection();
            connection.setClientID(name);
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            messageProducer = session.createProducer(queue);

            String selector = String.format("%s = '%s' AND %s = '%s'",
                    Names.PROP_CLUSTER, cluster, Names.PROP_COMPLEXITY, complexity);
            messageConsumer = session.createDurableSubscriber(topic, name, selector, false);
            messageConsumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    Computer.this.onMessage(message);
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
            case Names.MSG_SRV_ASSIGN:
                handleSrvAssign(m.getObject());
                break;
            default:
                System.err.printf("Invalid message type received: %d%n", type);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    private void handleSrvAssign(Serializable object) {
        final ProcessTaskDTO dto = (ProcessTaskDTO)object;
        if (dto == null) {
            System.err.println("Invalid body received");
            return;
        }

        if (listener == null) {
            return;
        }

        listener.waitTillProcessed(dto, name, dto.getComplexity(), dto.getRatedBy());

        try {
            dto.setStatus(TaskStatus.PROCESSED);

            ObjectMessage msg = session.createObjectMessage(dto);
            msg.setIntProperty(Names.PROP_TYPE, Names.MSG_COMP_PROCESSED);
            messageProducer.send(msg);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try { if (messageConsumer != null) messageConsumer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (messageProducer != null) messageProducer.close(); } catch (JMSException e) { e.printStackTrace(); }
        try { if (session != null){ session.unsubscribe(name); session.close(); } } catch (JMSException e) { e.printStackTrace(); }
        try { if (connection != null) connection.close(); } catch (JMSException e) { e.printStackTrace(); }
    }

    @Override
    public void setComputerListener(IComputerListener listener) {
        this.listener = listener;
    }

}
