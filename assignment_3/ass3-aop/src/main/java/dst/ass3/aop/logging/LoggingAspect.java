package dst.ass3.aop.logging;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggingAspect {

    @Pointcut("execution(* dst.ass3.aop.IPluginExecutable.execute(..)) && !@annotation(Invisible)")
    void anyCall() { }

    @Before("anyCall()")
    public void beforeAnyCall(JoinPoint thisJoinPoint) {
        logExecution(thisJoinPoint, "Before");
    }

    @After("anyCall()")
    public void afterAnyCall(JoinPoint thisJoinPoint) {
        logExecution(thisJoinPoint, "After");
    }

    private void logExecution(JoinPoint thisJoinPoint, String prefix) {
        String msg = String.format("%s %s%n", prefix, thisJoinPoint.getTarget());

        Logger logger = extractLogger(thisJoinPoint.getTarget());
        if (logger != null) {
            logger.setLevel(Level.INFO);
            logger.info(msg);
        } else {
            System.out.println(msg);
        }
    }

    private Logger extractLogger(Object object) {
        Class<?> clazz = object.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            if (!Logger.class.isAssignableFrom(field.getType())) {
                continue;
            }

            final boolean prev = field.isAccessible();
            try {
                field.setAccessible(true);
                return (Logger)field.get(object);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } finally {
                field.setAccessible(prev);
            }
        }

        return null;
    }

}
