package dst.ass3.aop;

import static dst.ass3.aop.util.PluginUtils.ALL_FILE;
import static dst.ass3.aop.util.PluginUtils.PLUGINS_DIR;
import static dst.ass3.aop.util.PluginUtils.SIMPLE_FILE;
import static dst.ass3.aop.util.PluginUtils.cleanPluginDirectory;
import static dst.ass3.aop.util.PluginUtils.preparePlugin;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dst.ass3.aop.event.Event;
import dst.ass3.aop.event.EventBus;
import dst.ass3.aop.event.EventType;

public class PluginTest {
	static final String SIMPLE_PLUGIN = "dst.ass3.aop.sample.SimplePluginExecutable";
	IPluginExecutor executor;
	EventBus eventBus = EventBus.getInstance();

	@BeforeClass
	public static void beforeClass() {
		assertEquals("Cannot create temporary plugin directory: " + PLUGINS_DIR.getAbsolutePath(),
				true, PLUGINS_DIR.isDirectory() || PLUGINS_DIR.mkdirs());
	}

	@AfterClass
	public static void afterClass() throws IOException {
		FileUtils.forceDeleteOnExit(PLUGINS_DIR);
	}

	@Before
	public void before() {
		cleanPluginDirectory();
		executor = PluginExecutorFactory.createPluginExecutor();
		executor.monitor(PLUGINS_DIR);
		executor.start();
		eventBus.reset();
	}

	@After
	public void after() {
		executor.stop();
		eventBus.reset();
		cleanPluginDirectory();
	}

	/**
	 * Executing plugin copied to plugin directory.
	 */
	@Test(timeout = 8000)
	public void testPlugin() throws Exception {
		// Preparing new plugin
		preparePlugin(SIMPLE_FILE);

		// Periodically check for the plugin to be executed
		while (eventBus.size() != 2) {
			Thread.sleep(100);
		}

		// Verify that the plugin was started and stopped orderly
		assertTrue(SIMPLE_PLUGIN + " was not started properly.", eventBus.has(SIMPLE_PLUGIN, EventType.PLUGIN_START));
		assertTrue(SIMPLE_PLUGIN + " did not finish properly.", eventBus.has(SIMPLE_PLUGIN, EventType.PLUGIN_END));
	}

	/**
	 * Checking that each plugin JAR uses its own ClassLoader.
	 */
	@Test(timeout = 8000)
	public void testSamePlugin() throws Exception {
		// Preparing two plugins
		preparePlugin(SIMPLE_FILE);
		preparePlugin(SIMPLE_FILE);

		// Periodically check for the plugins to be executed
		while (eventBus.size() != 4) {
			Thread.sleep(100);
		}

		/*
		 * Verify that the plugins were loaded by different classloaders.
		 * This can be checked by comparing the ClassLoaders or comparing the classes themselves.
		 * In other words, if a class is loaded by two different ClassLoaders, it holds that
		 * a.getClass() != b.getClass() even if the byte code is identical.
		 */
		List<Event> events = eventBus.getEvents();
		String msg = "Both plugins where loaded by the same ClassLoader";
		assertNotSame(msg, events.get(0).getPluginClass().getClassLoader(), events.get(1).getPluginClass().getClassLoader());
		assertNotSame(msg, events.get(0).getPluginClass(), events.get(1).getPluginClass());
	}

	/**
	 * Checking whether two plugins in a single JAR are executed concurrently.
	 */
	@Test(timeout = 8000)
	public void testConcurrentExecution() throws Exception {
		// Start a plugin containing two IPluginExecutable classes
		preparePlugin(ALL_FILE);

		// Periodically check for the plugins to be executed
		while (eventBus.size() != 4) {
			Thread.sleep(100);
		}

		// Check that there is exactly one start and end event each
		List<Event> starts = eventBus.getEvents(EventType.PLUGIN_START);
		List<Event> ends = eventBus.getEvents(EventType.PLUGIN_END);
		assertEquals("EventBus must contain exactly 2 start events.", 2, starts.size());
		assertEquals("EventBus must contain exactly 2 end events.", 2, ends.size());

		// Verify that the plugins were started concurrently
		String msg = "All plugins should have been started before the first ended - %d was after %d.";
		for (Event end : ends) {
			for (Event start : starts) {
				assertTrue(String.format(msg, start.getTime(), end.getTime()), start.getTime() < end.getTime());
			}
		}
	}

	/**
	 * Checking whether two plugin JARs are executed concurrently.
	 */
	@Test(timeout = 8000)
	public void testConcurrentExecutionTwoPlugins() throws Exception {
		// Start two plugins at once
		preparePlugin(SIMPLE_FILE);
		preparePlugin(SIMPLE_FILE);

		// Periodically check for the plugins to be executed
		while (eventBus.size() != 4) {
			Thread.sleep(100);
		}

		// Check that there is exactly one start and end event each
		List<Event> starts = eventBus.getEvents(EventType.PLUGIN_START);
		List<Event> ends = eventBus.getEvents(EventType.PLUGIN_END);
		assertEquals("EventBus must contain exactly 2 start events.", 2, starts.size());
		assertEquals("EventBus must contain exactly 2 end events.", 2, ends.size());

		// Verify that the plugins were started concurrently.
		String msg = "All plugins should have been started before the first ended - %d was after %d.";
		for (Event end : ends) {
			for (Event start : starts) {
				assertTrue(String.format(msg, start.getTime(), end.getTime()), start.getTime() < end.getTime());
			}
		}
	}
}
