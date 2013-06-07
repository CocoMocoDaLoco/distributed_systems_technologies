package dst.ass3.event.impl;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

import dst.ass3.dto.TaskDTO;
import dst.ass3.event.Constants;
import dst.ass3.event.IEventProcessing;
import dst.ass3.model.ITask;
import dst.ass3.model.TaskStatus;

public class EventProcessing implements IEventProcessing {

    private static final String PROVIDER_NAME = "EsperEngineDST";
    private static final String EVENT_TASK = "Task";
    private static final int WINDOW_SIZE = 10000;

    private EPServiceProvider serviceProvider;
    private EPRuntime runtime;

    @Override
    public void initializeAll(StatementAwareUpdateListener listener, boolean debug) {
        Configuration config = new Configuration();
        config.addEventType(EVENT_TASK, TaskDTO.class);
        config.addImport(TaskStatus.class);

        if (debug) {
            config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
            config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
            config.getEngineDefaults().getLogging().setEnableQueryPlan(false);
        }

        serviceProvider = EPServiceProviderManager.getProvider(PROVIDER_NAME, config);
        runtime = serviceProvider.getEPRuntime();
        EPAdministrator administrator = serviceProvider.getEPAdministrator();

        createTypes(listener, administrator);
        createQueries(listener, administrator);
    }

    private void createQueries(StatementAwareUpdateListener listener, EPAdministrator administrator) {
        String epl = String.format("insert into %s (jobId, timestamp) " +
                            "select jobId, current_timestamp() " +
                            "from %s " +
                            "where status = %s.%s",
                Constants.EVENT_TASK_ASSIGNED,
                EVENT_TASK,
                TaskStatus.class.getSimpleName(),
                TaskStatus.ASSIGNED.toString());
        administrator.createEPL(epl);

        epl = String.format("insert into %s (jobId, timestamp) " +
                            "select jobId, current_timestamp() " +
                            "from %s " +
                            "where status = %s.%s",
                Constants.EVENT_TASK_PROCESSED,
                EVENT_TASK,
                TaskStatus.class.getSimpleName(),
                TaskStatus.PROCESSED.toString());
        administrator.createEPL(epl);

        epl = String.format("insert into %s (jobId, duration) " +
                            "select a.jobId, b.timestamp - a.timestamp " +
                            "from %s.win:length(%d) a, " +
                            "     %s.win:length(%d) b " +
                            "where a.jobId = b.jobId",
                Constants.EVENT_TASK_DURATION,
                Constants.EVENT_TASK_ASSIGNED,
                WINDOW_SIZE,
                Constants.EVENT_TASK_PROCESSED,
                WINDOW_SIZE);
        administrator.createEPL(epl);

        epl = String.format("insert into %s (%s) " +
                            "select avg(duration) " +
                            "from %s.win:time(15 seconds)",
                Constants.EVENT_AVG_TASK_DURATION,
                Constants.EVENT_AVG_TASK_DURATION,
                Constants.EVENT_TASK_DURATION);
        administrator.createEPL(epl);

        String pattern = String.format("every r1=%1$s(status = %2$s.%3$s) @consume(1) -> " +
                                       "         %1$s(status = %2$s.%4$s, id = r1.id) @consume(2) -> " +
                                       "         %1$s(status = %2$s.%3$s, id = r1.id) @consume(3) -> " +
                                       "         %1$s(status = %2$s.%4$s, id = r1.id) @consume(4) -> " +
                                       "         %1$s(status = %2$s.%3$s, id = r1.id) @consume(5) -> " +
                                       "         %1$s(status = %2$s.%4$s, id = r1.id) @consume(6)",
                EVENT_TASK,
                TaskStatus.class.getSimpleName(),
                TaskStatus.READY_FOR_PROCESSING.toString(),
                TaskStatus.PROCESSING_NOT_POSSIBLE.toString());
        administrator.createPattern(pattern).addListener(listener);
    }

    private void createTypes(StatementAwareUpdateListener listener, EPAdministrator administrator) {
        String epl = String.format("create schema %s as (jobId long, timestamp long)",
                Constants.EVENT_TASK_ASSIGNED);
        administrator.createEPL(epl);

        epl = String.format("select * from %s", Constants.EVENT_TASK_ASSIGNED);
        administrator.createEPL(epl).addListener(listener);

        epl = String.format("create schema %s as (jobId long, timestamp long)",
                Constants.EVENT_TASK_PROCESSED);
        administrator.createEPL(epl);

        epl = String.format("select * from %s", Constants.EVENT_TASK_PROCESSED);
        administrator.createEPL(epl).addListener(listener);

        epl = String.format("create schema %s as (jobId long, duration long)",
                Constants.EVENT_TASK_DURATION);
        administrator.createEPL(epl);

        epl = String.format("select * from %s", Constants.EVENT_TASK_DURATION);
        EPStatement statement = administrator.createEPL(epl);
        statement.addListener(listener);
        statement.addListener(new PrintListener());

        epl = String.format("create schema %s as (%s double)",
                Constants.EVENT_AVG_TASK_DURATION,
                Constants.EVENT_AVG_TASK_DURATION);
        administrator.createEPL(epl);

        epl = String.format("select * from %s", Constants.EVENT_AVG_TASK_DURATION);
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

    private static class PrintListener implements StatementAwareUpdateListener {
        @Override
        public void update(EventBean[] newEvents, EventBean[] oldEvents, EPStatement statement,
                EPServiceProvider epServiceProvider) {
            for (EventBean e : newEvents) {
                System.out.println(e);
            }
        }
    }

}
