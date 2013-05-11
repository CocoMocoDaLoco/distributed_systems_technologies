package dst.ass3.event;

import static dst.ass3.EventingUtils.ESPER_CHECK_TIMEOUT;
import static dst.ass3.util.Utils.SHORT_WAIT;
import static dst.ass3.util.Utils.assure;
import static dst.ass3.util.Utils.logCheckpoint;
import static dst.ass3.util.Utils.logTimed;
import static dst.ass3.util.Utils.sleep;
import static org.junit.Assert.fail;

import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.WriterAppender;
import org.junit.Test;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

import dst.ass3.AbstractEventTest;
import dst.ass3.model.ITask;
import dst.ass3.model.TaskComplexity;
import dst.ass3.model.TaskStatus;

/**
 * Checks if only DTOs are used as Esper objects no hibernate annotations should
 * be present.
 * 
 * IMPORTANT! Enable the following Esper loggers for this test to work.
 * 
 * <pre>
 * 
 * config.getEngineDefaults().getLogging().setEnableExecutionDebug(true);
 * config.getEngineDefaults().getLogging().setEnableTimerDebug(false);
 * config.getEngineDefaults().getLogging().setEnableQueryPlan(false);
 * </pre>
 * 
 */
public class Test2 extends AbstractEventTest {

    private Semaphore sem;
    private StringWriter log4jWriter;

    @Test
    public void test_DTOUsed() {
        final long startTime = System.currentTimeMillis();
        this.log4jWriter = new StringWriter();
        Appender appender = new WriterAppender(new SimpleLayout(), log4jWriter);
        Logger.getRootLogger().addAppender(appender);

        // DEBUG enable this to see the full log output
        // Logger.getRootLogger().addAppender(new ConsoleAppender(new
        // SimpleLayout()));
        Logger.getRootLogger().setLevel((Level) Level.DEBUG);

        sem = new Semaphore(0);

        ITask t1 = EventingFactory.createTask(201L, 211L, TaskStatus.ASSIGNED,
                "c1", TaskComplexity.EASY);

        test.initializeAll(new StatementAwareUpdateListener() {

            @Override
            public void update(EventBean[] newEvents, EventBean[] oldEvents,
                    EPStatement s, EPServiceProvider p) {
                System.out.println("LISTENER CALLED");

                for (EventBean e : newEvents) {
                    String name = e.getEventType().getName();
                    if (name.equals(Constants.EVENT_TASK_DURATION)) {
                        sem.release();
                    }
                }

            }
        }, true);

        sleep(SHORT_WAIT * 10); /*
                                 * wait 10 times longer than short waiting for
                                 * the log system to start up
                                 */
        for (String logLine : getLogLine(".makeFilterSpec spec=FilterSpecCompiled")) {
            Pattern p = Pattern
                    .compile("(dst3\\.messaging\\.([\\.a-zA-Z0-9])*)");
            Matcher matcher = p.matcher(logLine);
            if (matcher.find()) {
                String clazzName = matcher.group(1);
                try {
                    System.out.println("Checking persistence Annotations of "
                            + clazzName);
                    Class<?> clazz = Class.forName(clazzName);
                    if (clazz.getAnnotation(Entity.class) != null) {
                        fail("Use only DTOs for Esper! (jpa annotation found in esper event class");
                    }
                    for (Field f : clazz.getFields()) {
                        for (Annotation a : f.getAnnotations()) {
                            if (a.getClass().getPackage().toString()
                                    .startsWith("javax.persistence")) {
                                fail("Use only DTOs for Esper! (jpa annotation found in esper event class");
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        logCheckpoint(0, startTime);
        test.addEvent(t1);

        logCheckpoint(1, startTime);
        t1.setStatus(TaskStatus.PROCESSED);
        test.addEvent(t1);

        logTimed("checking results", startTime);
        assure(sem, 1, "1 taskDuration event expected!", ESPER_CHECK_TIMEOUT); // expect
        // TaskDuration
        // event

    }

    public Collection<String> getLogLine(String... parts) {
        Collection<String> ret = new ArrayList<String>();
        log4jWriter.flush();
        String log4j = log4jWriter.toString();
        String lines[] = log4j.split("\n");
        for (String line : lines) {
            boolean contains = true;
            for (String part : parts) {
                if (!line.contains(part)) {
                    contains = false;
                    break;
                }
                if (contains) {
                    // System.out.println("found LINE: "+line);
                    ret.add(line);
                }
            }
        }
        return ret;
    }
}
