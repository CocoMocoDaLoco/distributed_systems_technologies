package dst.ass3.aop.management;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

import dst.ass3.aop.IPluginExecutable;

@Aspect
public class ManagementAspect {

    private final Timer timer = new Timer();
    private final List<IPluginExecutable> activeExecutions = Collections.synchronizedList(new ArrayList<IPluginExecutable>());

    @Pointcut("execution(* dst.ass3.aop.IPluginExecutable.execute(..)) && @annotation(Timeout)")
    void timeoutExecution() { }

    @Before("timeoutExecution()")
    public void beforeTimeoutExecution(JoinPoint thisJoinPoint) {
        final IPluginExecutable executable = (IPluginExecutable)thisJoinPoint.getTarget();

        activeExecutions.add(executable);

        try {
            Method method = thisJoinPoint.getTarget().getClass().getMethod("execute");
            Timeout timeout = method.getAnnotation(Timeout.class);

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (activeExecutions.contains(executable)) {
                        executable.interrupted();
                    }
                }
            }, timeout.value());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @After("timeoutExecution()")
    public void afterTimeoutExecution(JoinPoint thisJoinPoint) {
        activeExecutions.remove(thisJoinPoint.getTarget());
    }

}
