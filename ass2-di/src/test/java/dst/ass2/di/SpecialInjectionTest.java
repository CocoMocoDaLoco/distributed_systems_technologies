package dst.ass2.di;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.ReflectionUtils;

import dst.ass2.di.type.ComplexComponent;
import dst.ass2.di.type.Container;
import dst.ass2.di.type.SimpleSingleton;
import dst.ass2.di.type.SubType;
import dst.ass2.di.type.SuperType;
import dst.ass2.di.util.InjectionUtils;

public class SpecialInjectionTest {
	private IInjectionController ic;

	@Before
	public void before() {
		ic = InjectionControllerFactory.getNewStandaloneInstance();
	}

	/**
	 * Attempts to create and initialize two singletons of the same time.
	 */
	@Test
	public void testInitializeSingletonTwice() {
		// Initializing a singleton component also registers it for further use
		ic.initialize(new SimpleSingleton());
		try {
			// Initializing a singleton of the same type results in an InjectionException
			ic.initialize(new SimpleSingleton());
			fail(InjectionException.class.getSimpleName() + " expected.");
		} catch (InjectionException e) {
			// expected
		}
	}

	/**
	 * Attempts to initialize a singleton manually twice and retrieves it from the {@link IInjectionController}.
	 */
	@Test
	public void testInitializeSingletonManually() {
		SimpleSingleton singleton = new SimpleSingleton();
		ic.initialize(singleton);
		try {
			// Initializing a singleton instance twice results in an InjectionException
			ic.initialize(singleton);
			fail(InjectionException.class.getSimpleName() + " expected.");
		} catch (InjectionException e) {
			// expected
		}

		// Retrieving a singleton of a certain type always returns the same object instance.
		assertSame("Singletons must be the same object instance.", singleton, ic.getSingletonInstance(SimpleSingleton.class));
	}

	/**
	 * Initializes a prototype twice.
	 * <p/>
	 * Every time a prototype component is initialized, all fields annotated with
	 * {@link dst.ass2.di.annotation.Inject Inject} become injected again. In other words, for every prototype the
	 * {@link IInjectionController} creates a new instance and replaces the reference to the the previous one.<br/>
	 * Finally, if nothing went wrong, the component gets a new component ID indicating that all fields have been
	 * re-injected successfully.
	 */
	@Test
	public void testInitializePrototypeTwice() {
		// Create an initialize the container
		Container container = new Container();
		ic.initialize(container);

		// Create a copy of the container by copying the object references for later comparison.
		Container copy = new Container();
		ReflectionUtils.shallowCopyFieldState(container, copy);

		// Re-initialize the same object
		ic.initialize(container);

		// Verify that the object still references the same singleton.
		assertSame("Both singleton references must be the same object instance.", copy.first, container.first);
		assertSame("Both singleton references must be the same object instance.", copy.second, container.second);

		// Verify that the prototype references have been changed.
		assertNotSame("Prototype components must be different.", copy.component, container.component);
		assertNotSame("Prototype components must be different.", copy.anotherComponent, container.anotherComponent);

		// Verify that the IDs are different
		assertTrue("The ID after the first initialization must be lower or equal to 3.", copy.id <= 3);
		assertTrue("The ID after the second initialization must be between 4 and 6 inclusive.", 4 <= container.id && container.id <= 6);

		// Verify that the IDs of the prototype components are correct
		String afterFirst = "The ID of '%s' after the first initialization must be lower or equal to 3.";
		String afterSecond = "The ID of '%s' after the second initialization must be between 4 and 6 inclusive.";

		// Due to the fact that Container consists of two prototypes and one singleton, which is referenced twice,
		// initializing the first Container requires 4 IDs and every additional instance 3 IDs
		assertTrue(String.format(afterFirst, "component"), copy.component.id <= 3);
		assertTrue(String.format(afterSecond, "component"), 4 <= container.component.id && container.component.id <= 6);
		assertTrue(String.format(afterFirst, "anotherComponent"), copy.anotherComponent.id <= 3);
		assertTrue(String.format(afterSecond, "anotherComponent"), 4 <= container.anotherComponent.id && container.anotherComponent.id <= 6);
	}

	/**
	 * Tests the type qualifier of the {@link dst.ass2.di.annotation.Inject} annotation.
	 */
	@Test
	public void testQualifier() {
		ComplexComponent component = new ComplexComponent();
		ic.initialize(component);

		// Required components

		assertNotNull("'id' must not be null.", component.id);
		// singleton is a required field
		assertNotNull("'singleton' must not be null.", component.singleton);
		// unknownSingleton is of type Object but requires the specific type SimpleSingleton
		assertNotNull("'unknownSingleton' must not be null.", component.unknownSingleton);
		assertSame("Both singleton references must be the same object instance.", component.singleton, component.unknownSingleton);

		// Optional components

		// theVoid is of type Void
		assertNull("'theVoid must be null.'", component.theVoid);
		// type Invalid, which is not a valid component
		assertNull("'invalid' must be null.'", component.invalid);
		// type SimpleComponent but requires SimpleSingleton which results in a ClassCastException
		assertNull("'singletonPrototype' must be null.", component.singletonPrototype);
	}

	/**
	 * Tests whether private hidden fields of super types are injected.
	 */
	@Test
	public void testInheritance() {
		// When initializing an object, all fields are initialized (even those, which are not visible).
		SubType subType = new SubType();
		ic.initialize(subType);

		// Retrieving all declared fields of the SubType and SuperType
		Map<String, Object> subFields = InjectionUtils.getFields(subType, SubType.class);
		Map<String, Object> superFields = InjectionUtils.getFields(subType, SuperType.class);

		// Verify that the component IDs are set
		assertNotNull("'id' of SubType must not be null.", subFields.get("id"));
		assertNotNull("'id' of SuperType must not be null.", superFields.get("id"));
		// The component IDs must be different
		assertTrue("The 'id's of SuperType and SubType must be equal.", subFields.get("id").equals(superFields.get("id")));

		// Verify that all fields are initialized properly
		assertNotNull("'component' of SubType must not be null.", subFields.get("component"));
		assertNotNull("'component' of SuperType must not be null.", superFields.get("component"));
		// The injected objects must be different
		assertFalse("The 'component's of SuperType and SubType must not be the same object instance.", subFields.get("component") == superFields.get("component"));
	}
}
