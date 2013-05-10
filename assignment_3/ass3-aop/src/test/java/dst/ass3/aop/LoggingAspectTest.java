package dst.ass3.aop;

import dst.ass3.aop.event.Event;
import dst.ass3.aop.event.EventBus;
import dst.ass3.aop.event.EventType;
import dst.ass3.aop.logging.Invisible;
import dst.ass3.aop.logging.LoggingAspect;
import dst.ass3.aop.sample.InvisiblePluginExecutable;
import dst.ass3.aop.sample.LoggingPluginExecutable;
import dst.ass3.aop.sample.SystemOutPluginExecutable;
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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static dst.ass3.aop.util.PluginUtils.*;
import static org.junit.Assert.*;

public class LoggingAspectTest {
	final EventBus eventBus = EventBus.getInstance();

	@org.junit.Before
	@org.junit.After
	public void beforeAndAfter() {
		eventBus.reset();
	}

	/**
	 * Verifies that the {@link LoggingAspect} is a valid AspectJ aspect i.e., {@link Aspect @Aspect} as well as
	 * {@link Around @Around} or {@link Before @Before} / {@link After @After}.
	 */
	@Test
	public void testLoggingAspectAnnotations() {
		Aspect aspect = AnnotationUtils.findAnnotation(LoggingAspect.class, Aspect.class);
		assertNotNull("LoggingAspect is not annotated with @Aspect", aspect);

		Map<Method, Around> around = findMethodAnnotation(LoggingAspect.class, Around.class);
		Map<Method, Before> before = findMethodAnnotation(LoggingAspect.class, Before.class);
		Map<Method, After> after = findMethodAnnotation(LoggingAspect.class, After.class);

		boolean found = !around.isEmpty() || (!before.isEmpty() && !after.isEmpty());
		assertTrue("LoggingAspect does not contain methods annotated with @Around OR @Before / @After", found);
	}

	/**
	 * Verifies that the pointcut expression of the {@link LoggingAspect} does not match any method except the
	 * {@link dst.ass3.aop.IPluginExecutable#execute()} method.
	 */
	@Test
	public void testLoggingAspectPointcutExpression() {
		IPluginExecutable executable = getExecutable(LoggingPluginExecutable.class, LoggingAspect.class);
		assertTrue("Executable must implement the Advised interface", executable instanceof Advised);
		Advised advised = (Advised) executable;

		PointcutAdvisor pointcutAdvisor = getPointcutAdvisor(advised);
		assertNotNull("PointcutAdvisor not found because there is no pointcut or the pointcut does not match", pointcutAdvisor);

		String expression = getBestExpression(advised);
		assertTrue("Pointcut expression must include '" + IPluginExecutable.class.getName() + "'", expression.contains(IPluginExecutable.class.getName()));
		assertTrue("Pointcut expression must include '" + EXECUTE_METHOD.getName() + "'", expression.contains(EXECUTE_METHOD.getName()));

		PointcutExpressionImpl pointcutExpression = getPointcutExpression(advised);
		ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(EXECUTE_METHOD);
		assertTrue("Pointcut does not match IPluginExecute.execute()", shadowMatch.alwaysMatches());

		shadowMatch = pointcutExpression.matchesMethodExecution(INTERRUPTED_METHOD);
		assertTrue("Pointcut must not match IPluginExecute.interrupted()", shadowMatch.neverMatches());

		shadowMatch = pointcutExpression.matchesMethodExecution(ReflectionUtils.findMethod(getClass(), EXECUTE_METHOD.getName()));
		assertTrue("Pointcut must not match LoggingPluginTest.execute()", shadowMatch.neverMatches());
	}

	/**
	 * Verifies that the pointcut expression of the LoggingAspect contains the {@link Invisible @Invisible} annotation.
	 */
	@Test
	public void testInvisibleAnnotation() {
		IPluginExecutable executable = getExecutable(LoggingPluginExecutable.class, LoggingAspect.class);
		Advised advised = (Advised) executable;

		String expression = getBestExpression(advised);
		String annotationName = Invisible.class.getName();
		assertTrue("Pointcut expression does not contain " + annotationName, expression.contains(annotationName));
	}

	/**
	 * Verifies that the pointcut expression of the {@link LoggingAspect} does not match any method annotated with
	 * {@link Invisible @Invisible}.
	 */
	@Test
	public void testInvisiblePointcutExpression() {
		IPluginExecutable executable = getExecutable(LoggingPluginExecutable.class, LoggingAspect.class);
		Advised advised = (Advised) executable;

		PointcutExpressionImpl pointcutExpression = getPointcutExpression(advised);

		Method loggingMethod = ReflectionUtils.findMethod(LoggingPluginExecutable.class, EXECUTE_METHOD.getName());
		ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(loggingMethod);
		assertTrue("Pointcut does not match LoggingPluginExecutable.execute()", shadowMatch.alwaysMatches());

		Method invisibleMethod = ReflectionUtils.findMethod(InvisiblePluginExecutable.class, EXECUTE_METHOD.getName());
		shadowMatch = pointcutExpression.matchesMethodExecution(invisibleMethod);
		assertTrue("Pointcut matches InvisiblePluginExecutable.execute()", shadowMatch.neverMatches());
	}

	/**
	 * Tests if the {@link LoggingAspect} uses the {@link java.util.logging.Logger Logger} defined in the plugin.
	 */
	@Test
	public void testLogging() {
		IPluginExecutable executable = getExecutable(LoggingPluginExecutable.class, LoggingAspect.class);
		Advised advised = (Advised) executable;

		// Add handler end check that there are no events
		addBusHandlerIfNecessary(advised);
		assertEquals("EventBus must be empty", 0, eventBus.count(EventType.INFO));

		// Execute plugin and check that there are 2 events
		executable.execute();
		List<Event> events = eventBus.getEvents(EventType.INFO);
		assertEquals("EventBus must exactly contain 2 INFO events", 2, events.size());

		// Check if the logger contains the correct class name
		events = eventBus.getEvents(EventType.INFO);
		for (Event event : events) {
			assertEquals("Event message must contain the name of the " + LoggingAspect.class.getSimpleName(), LoggingAspect.class.getName(), event.getMessage());
			assertSame("Event must be logged for " + LoggingPluginExecutable.class.getSimpleName(), LoggingPluginExecutable.class, event.getPluginClass());
		}
	}

	/**
	 * Tests if the {@link LoggingAspect} uses {@code System.out} if the plugin does not contain a
	 * {@link java.util.logging.Logger Logger} field.
	 *
	 * @throws IllegalAccessException if {@code System.out} cannot be modified (must not happen)
	 */
	@Test
	public void testSystemOut() throws IllegalAccessException {
		// Redirect System.out
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PrintStream out = setStaticFinalField(System.class, "out", new PrintStream(byteArrayOutputStream));
		try {
			// Execute plugin
			IPluginExecutable executable = getExecutable(SystemOutPluginExecutable.class, LoggingAspect.class);
			assertEquals("EventBus must be empty", 0, eventBus.size());
			executable.execute();
			assertEquals("EventBus must exactly contain 2 events", 2, eventBus.size());

			// Verify that the log output contains the class name of the executed plugin
			String output = byteArrayOutputStream.toString();
			assertTrue(String.format("Log output must contain %s\n\tbut was%s", SystemOutPluginExecutable.class.getName(), output),
					output.contains(SystemOutPluginExecutable.class.getName()));
		} finally {
			// Reset System.out
			setStaticFinalField(System.class, "out", out);
		}
	}

	public void execute() {
	}
}
