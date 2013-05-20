package dst.ass3.jms.server;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Topic;

import dst.ass3.dto.TaskDTO;
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
