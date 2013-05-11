package dst.ass3.jms.scheduler;

import dst.ass3.dto.TaskDTO;

public interface IScheduler {
    /**
     * Starts a scheduler
     */
    void start();

    /**
     * Stops the scheduler and cleans all resources (e.g.: close session,
     * connection, etc.).
     */
    void stop();

    /**
     * Creates an new Job with the given ID.
     * 
     * @param jobId
     */
    void assign(long jobId);

    /**
     * Requests information for the given Task ID.
     * 
     * @param taskId
     */
    void info(long taskId);

    /**
     * Sets the listener to report incoming messages. Only one Listener should
     * be in use at any time. Be sure to handle cases where messages are
     * received but not listener is yet set (discard the message).
     * 
     * @param listener
     */
    void setSchedulerListener(ISchedulerListener listener);

    interface ISchedulerListener {
        enum InfoType {
            CREATED, INFO, PROCESSED, DENIED
        }

        /**
         * Notifies the listener about an incoming message.
         * 
         * @param type
         *            the type of the incoming message
         * @param task
         *            the task of the incoming message
         */
        void notify(InfoType type, TaskDTO task);
    }
}
