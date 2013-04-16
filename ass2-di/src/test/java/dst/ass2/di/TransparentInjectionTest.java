package dst.ass2.di;

import dst.ass2.di.type.Container;
import dst.ass2.di.type.SimpleSingleton;
import dst.ass2.di.util.InjectionUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TransparentInjectionTest {
    IInjectionController ic;

    @Before
    public void before() {
        ic = InjectionControllerFactory.getTransparentInstance();
    }

    /**
     * Injecting prototypes and singletons into an object.
     */
    @Test
    public void testInject() {
        Container container = new Container();

        assertNotNull("'timestamp' must not be null.", container.timestamp);
        Long timestamp = container.timestamp;

        assertSame("Initial timestamp was modified.", timestamp, container.timestamp);
        assertNotNull("'id' must not be null.", container.id);
        assertNotNull("'first' must not be null.", container.first);
        assertNotNull("'second' must not be null.", container.second);
        assertNotNull("'component' must not be null.", container.component);
        assertNotNull("'anotherComponent' must not be null.", container.anotherComponent);

        assertSame("Singletons must be the same object instance.", container.first, container.second);
        assertNotSame("Prototype references must not be the same object instance.", container.component, container.anotherComponent);

        List<Long> ids = InjectionUtils.getIds(container);
        Collections.sort(ids);
        assertEquals("Initialized object graph with 4 components.", 4, ids.size());

        for (long i = ids.get(0); i < ids.get(0) + ids.size(); i++) {
            assertTrue("There is no component with ID " + i + ".", ids.contains(i));
        }
    }

    /**
     * Injecting components into hierarchies.
     */
    @Test
    public void testHierarchy() throws IllegalAccessException {
        Container container = new Container();
        Long oldId = container.id;

        SimpleSingleton singleton = ic.getSingletonInstance(SimpleSingleton.class);
        assertNotNull("'id' must not be null.", singleton.id);

        // Check that the same singleton is used
        assertSame("Singletons must be the same object instance.", container.first, singleton);

        // Verify that exactly 4 new component IDs where used
        assertEquals("More than 4 components were instantiated with the container.", oldId + 3L, new Container().id.longValue());

        try {
            new SimpleSingleton();
            fail(InjectionException.class.getSimpleName() + " expected");
        } catch (InjectionException e) {
            // expected
        }
    }
}
