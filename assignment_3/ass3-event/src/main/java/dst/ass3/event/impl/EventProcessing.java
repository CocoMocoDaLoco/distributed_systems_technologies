package dst.ass3.event.impl;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.StatementAwareUpdateListener;

import dst.ass3.dto.TaskDTO;
import dst.ass3.event.Constants;
import dst.ass3.event.IEventProcessing;
import dst.ass3.model.ITask;

public class EventProcessing implements IEventProcessing {

    private static final String PROVIDER_NAME = "EsperEngineDST";

    private EPServiceProvider serviceProvider;
    private EPRuntime runtime;

    @Override
    public void initializeAll(StatementAwareUpdateListener listener, boolean debug) {
        Configuration config = new Configuration();
        config.addEventType(TaskDTO.class);

        if (debug) {
            config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
            config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
            config.getEngineDefaults().getLogging().setEnableQueryPlan(false);
        }

        serviceProvider = EPServiceProviderManager.getProvider(PROVIDER_NAME, config);
        runtime = serviceProvider.getEPRuntime();
        EPAdministrator administrator = serviceProvider.getEPAdministrator();

        String epl = String.format("create schema %s as (jobId long, timestamp long)",
                Constants.EVENT_TASK_ASSIGNED);
        administrator.createEPL(epl).addListener(listener);

        epl = String.format("create schema %s as (jobId long, timestamp long)",
                Constants.EVENT_TASK_PROCESSED);
        administrator.createEPL(epl).addListener(listener);

        epl = String.format("create schema %s as (jobId long, duration long)",
                Constants.EVENT_TASK_DURATION);
        administrator.createEPL(epl).addListener(listener);
    }

    @Override
    public void addEvent(ITask task) {
        runtime.sendEvent(new TaskDTO(task));
    }

    @Override
    public void close() {
        runtime = null;

        serviceProvider.destroy();
        serviceProvider = null;
    }

}
