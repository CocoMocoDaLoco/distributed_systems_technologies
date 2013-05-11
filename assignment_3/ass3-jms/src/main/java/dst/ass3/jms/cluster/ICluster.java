package dst.ass3.jms.cluster;

import dst.ass3.dto.RateTaskDTO;
import dst.ass3.model.TaskComplexity;

public interface ICluster {
    /**
     * Starts a cluster
     */
    void start();

    /**
     * Stops the cluster and cleans all resources (e.g.: close session,
     * connection, etc.). Keep in mind that Listeners may be sleeping when stop
     * is requested. Be sure to interrupt them and discard the results they
     * might return, because the system is stopping already.
     */
    void stop();

    /**
     * Sets the TaskListener. Only one TaskListener should be in use at any
     * time. Be sure to handle cases where messages are received but no
     * listener is yet set (discard the message). The listeners may block
     * forever, so be sure to interrupt them in stop().
     * 
     * @param listener
     */
    void setClusterListener(IClusterListener listener);

    interface IClusterListener {
        enum TaskResponse {
            ACCEPT, DENY
        };

        class TaskDecideResponse {
            public TaskResponse resp;
            public TaskComplexity complexity;

            public TaskDecideResponse(TaskResponse resp,
                    TaskComplexity complexity) {
                this.resp = resp;
                this.complexity = complexity;
            }
        }

        /**
         * Decide on the given task.
         * 
         * @param task
         *            the task to decide
         * @param clusterName
         *            name the cluster executing this listener
         * @return ACCEPT + Complexity | DENY
         */
        TaskDecideResponse decideTask(RateTaskDTO task, String clusterName);
    }
}
