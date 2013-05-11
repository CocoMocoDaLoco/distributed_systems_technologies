package dst.ass3.jms.scheduler.impl;

import dst.ass3.jms.scheduler.IScheduler;

public class Scheduler implements IScheduler {

    private ISchedulerListener listener;

    @Override
    public void start() {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop() {
        // TODO Auto-generated method stub

    }

    @Override
    public void assign(long jobId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void info(long taskId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setSchedulerListener(ISchedulerListener listener) {
        this.listener = listener;
    }

}
