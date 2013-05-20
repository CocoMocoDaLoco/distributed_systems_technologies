package dst.ass3.jms;

public class Names {
    public static final String CONNECTION_FACTORY = "dst.Factory";

    public static final String CLUSTER_QUEUE = "queue.dst.ClusterQueue";
    public static final String SCHEDULER_QUEUE = "queue.dst.SchedulerQueue";
    public static final String SERVER_QUEUE = "queue.dst.ServerQueue";

    public static final String COMPUTER_TOPIC = "queue.dst.ComputerTopic";

    public static final String PROP_TYPE = "MESSAGE_TYPE";
    public static final int MSG_SCHED_CREATE = 0;
    public static final int MSG_SRV_CREATED = 1;
    public static final int MSG_SRV_DECIDE = 2;
    public static final int MSG_SCHED_INFO = 3;
    public static final int MSG_SRV_INFO = 4;
    public static final int MSG_CLUSTER_DECISION = 5;
    public static final int MSG_SRV_DENIED = 6;
    public static final int MSG_SRV_ASSIGN = 7;

    private Names() { }
}
