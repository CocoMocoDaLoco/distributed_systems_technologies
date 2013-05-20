package dst.ass3.jms.server;

import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;

import dst.ass3.jms.Names;

@MessageDriven(mappedName = Names.SERVER_QUEUE)
public class ServerBean implements MessageListener {

    @Resource(mappedName = Names.CLUSTER_QUEUE)
    private Queue clusterQueue;

    @Resource(mappedName = Names.SCHEDULER_QUEUE)
    private Queue schedulerQueue;

    @Resource(mappedName = Names.COMPUTER_TOPIC)
    private Topic computerTopic;

    @Override
    public void onMessage(Message msg) {
        TextMessage tm = (TextMessage)msg;
        if (tm == null) {
            return;
        }

        try {
            System.out.println(tm.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
