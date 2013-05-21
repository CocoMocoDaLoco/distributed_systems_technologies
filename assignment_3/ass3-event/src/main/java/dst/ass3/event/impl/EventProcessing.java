package dst.ass3.event.impl;

import com.espertech.esper.client.StatementAwareUpdateListener;

import dst.ass3.event.IEventProcessing;
import dst.ass3.model.ITask;

public class EventProcessing implements IEventProcessing {

    private StatementAwareUpdateListener listener;

    @Override
    public void initializeAll(StatementAwareUpdateListener listener, boolean debug) {
        this.listener = listener;

        if (debug) {
            /* config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
             * config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
             * config.getEngineDefaults().getLogging().setEnableQueryPlan(false); */
        }
    }

    @Override
    public void addEvent(ITask task) {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
