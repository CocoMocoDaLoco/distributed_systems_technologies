package dst.ass3.aop;

import dst.ass3.aop.event.Event;
import dst.ass3.aop.event.EventBus;
import dst.ass3.aop.management.ManagementAspect;
import dst.ass3.aop.sample.InterruptedPluginExecutable;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.weaver.internal.tools.PointcutExpressionImpl;
import org.aspectj.weaver.tools.ShadowMatch;
import org.junit.Test;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.Advised;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static dst.ass3.aop.util.PluginUtils.*;
import static org.junit.Assert.*;

public class PerformanceAspectTest {
    final EventBus eventBus = EventBus.getInstance();

    @org.junit.Before
    @org.junit.After
    public void beforeAndAfter() {
        eventBus.reset();
    }

    /**
     * Verifies that the {@link ManagementAspect} is a valid AspectJ aspect i.e., {@link Aspect @Aspect} as well as
     * {@link Around @Around} or {@link Before @Before} / {@link After @After}.
     */
    @Test
    public void testPerformanceAspectAnnotations() {
        Aspect aspect = AnnotationUtils.findAnnotation(ManagementAspect.class, Aspect.class);
        assertNotNull("ManagementAspect is not annotated with @Aspect", aspect);

        Map<Method, Around> around = findMethodAnnotation(ManagementAspect.class, Around.class);
        Map<Method, Before> before = findMethodAnnotation(ManagementAspect.class, Before.class);
        Map<Method, After> after = findMethodAnnotation(ManagementAspect.class, After.class);

        boolean found = !around.isEmpty() || (!before.isEmpty() && !after.isEmpty());
        assertEquals("ManagementAspect does not contain methods annotated with @Around OR @Before and @After", true, found);
    }

    /**
     * Verifies that the pointcut expression of the {@link dst.ass3.aop.management.ManagementAspect}
     * does not match any method except the {@link dst.ass3.aop.IPluginExecutable#execute()} method.
     */
    @Test
    public void testPerformanceAspectPointcutExpression() {
        IPluginExecutable executable = getExecutable(InterruptedPluginExecutable.class, ManagementAspect.class);
        assertEquals("Executable must implement the Advised interface", true, executable instanceof Advised);
        Advised advised = (Advised) executable;

        PointcutAdvisor pointcutAdvisor = getPointcutAdvisor(advised);
        assertNotNull("PointcutAdvisor not found because there is no pointcut or the pointcut does not match", pointcutAdvisor);

        PointcutExpressionImpl pointcutExpression = getPointcutExpression(advised);
        Method interruptedMethod = ReflectionUtils.findMethod(InterruptedPluginExecutable.class, EXECUTE_METHOD.getName());
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(interruptedMethod);
        assertEquals("Pointcut does not match InterruptedPluginExecutable.execute()", true, shadowMatch.alwaysMatches());
    }

    /**
     * Tests if the {@link ManagementAspect} interrupts the plugin after the given timeout.
     */
    @Test(timeout = 8000)
    public void testInterrupted() {
        IPluginExecutable executable = getExecutable(InterruptedPluginExecutable.class, ManagementAspect.class);
        assertEquals("EventBus must be empty", 0, eventBus.size());
        executable.execute();

        List<Event> events = eventBus.getEvents();
        assertEquals("EventBus must contain 2 events", 2, events.size());

        long duration = events.get(1).getTime() - events.get(0).getTime();
        assertTrue("Plugin was not interrupted 2 seconds after starting it", duration < 3000);
    }
}
