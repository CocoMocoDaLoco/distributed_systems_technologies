package dst.ass3.jms;

import dst.ass3.jms.cluster.ICluster;
import dst.ass3.jms.computer.IComputer;
import dst.ass3.jms.scheduler.IScheduler;
import dst.ass3.model.TaskComplexity;

public class JMSFactory {
    
    public static ICluster createCluster(String name) {
        // TODO
        return null;
    }

    public static IComputer createComputer(String name, String cluster,
            TaskComplexity complexity) {
        // TODO
        return null;
    }

    public static IScheduler createScheduler() {
        // TODO
        return null;
    }

}
