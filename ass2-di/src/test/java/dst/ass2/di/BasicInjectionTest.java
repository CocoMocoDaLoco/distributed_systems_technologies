package dst.ass2.di;

import dst.ass2.di.type.Container;
import dst.ass2.di.type.Invalid;
import dst.ass2.di.type.SimpleComponent;
import dst.ass2.di.type.SimpleSingleton;
import dst.ass2.di.util.InjectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class BasicInjectionTest {
	private IInjectionController ic;

	@Before
	public void before() {
		ic = InjectionControllerFactory.getNewStandaloneInstance();
	}

	/**
	 * Initializing two prototype components.
	 */
	@Test
	public void testSimpleComponent() {
		SimpleComponent first = new SimpleComponent();
		assertNull("The ID of an uninitialized component must be null", first.id);

		ic.initialize(first);
		assertEquals("The ID of the first component must be 0", Long.valueOf(0L), first.id);

		SimpleComponent second = new SimpleComponent();
		ic.initialize(second);
		assertEquals("The ID of the second component must be 1", Long.valueOf(1L), second.id);
	}

	/**
	 * Retrieving a singleton twice.
	 */
	@Test
	public void testSimpleSingleton() {
		SimpleSingleton first = ic.getSingletonInstance(SimpleSingleton.class);
		SimpleSingleton second = ic.getSingletonInstance(SimpleSingleton.class);

		assertNotNull("The ID of the singleton must not be null.", first.id);
		assertEquals("Singleton has wrong ID.", Long.valueOf(0L), first.id);
		assertSame("Singletons must be the same object instance.", first, second);
	}

	/**
	 * Injecting prototypes and singletons into an object.
	 */
	@Test
	public void testInject() {
		Container container = new Container();

		assertNotNull("'timestamp' must not be null.", container.timestamp);
		Long timestamp = container.timestamp;

		assertNull("'id' must be null.", container.id);
		assertNull("'first' must be null.", container.first);
		assertNull("'second' must be null.", container.second);
		assertNull("'component' must ne null.", container.component);
		assertNull("'anotherComponent' must be null.", container.anotherComponent);

		ic.initialize(container);
		assertSame("Initial timestamp was modified.", timestamp, container.timestamp);
		assertNotNull("'id' must not be null.", container.id);
		assertNotNull("'first' must not be null.", container.first);
		assertNotNull("'second' must not be null.", container.second);
		assertNotNull("'component' must not be null.", container.component);
		assertNotNull("'anotherComponent' must not be null.", container.anotherComponent);

		assertSame("Singletons must be the same object instance.", container.first, container.second);
		assertNotSame("Prototype references must not be the same object instance.", container.component, container.anotherComponent);

		Collection<Long> ids = InjectionUtils.getIds(container);
		assertEquals("Initialized object graph with 4 components.", 4, ids.size());

		for (long i = 0; i < ids.size(); i++) {
			assertTrue("There is no component with ID " + i + ".", ids.contains(i));
		}
	}

	/**
	 * Attempts to initialize an object that is not annotated with {@link dst.ass2.di.annotation.Component @Component}.
	 */
	@Test(expected = InjectionException.class)
	public void testInvalidPrototype() {
		ic.initialize(new Invalid());
	}

	/**
	 * Attempts to retrieve a singleton instance of a class that is not annotated with
	 * {@link dst.ass2.di.annotation.Component @Component}.
	 */
	@Test(expected = InjectionException.class)
	public void testInvalidSingleton() {
		ic.getSingletonInstance(Invalid.class);
	}

	/**
	 * Attempts to retrieve a singleton instance of a prototype component.
	 */
	@Test(expected = InjectionException.class)
	public void testPrototypeAsSingleton() {
		ic.getSingletonInstance(SimpleComponent.class);
	}
}
