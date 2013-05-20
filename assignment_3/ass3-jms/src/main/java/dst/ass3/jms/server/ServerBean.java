package dst.ass3.jms.server;

import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import dst.ass3.jms.Names;

@MessageDriven(mappedName = Names.SERVER_QUEUE)
public class ServerBean implements MessageListener {

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
