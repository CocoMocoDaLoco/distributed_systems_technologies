package dst.ass3.jms;

import dst.ass3.jms.cluster.ICluster;
import dst.ass3.jms.cluster.impl.Cluster;
import dst.ass3.jms.computer.IComputer;
import dst.ass3.jms.computer.impl.Computer;
import dst.ass3.jms.scheduler.IScheduler;
import dst.ass3.jms.scheduler.impl.Scheduler;
import dst.ass3.model.TaskComplexity;

public class JMSFactory {

    public static ICluster createCluster(String name) {
        return new Cluster(name);
    }

    public static IComputer createComputer(String name, String cluster, TaskComplexity complexity) {
        return new Computer(name, cluster, complexity);
    }

    public static IScheduler createScheduler() {
        return new Scheduler();
    }

}
