package dst.ass2.di.util;

import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;
import dst.ass2.di.annotation.Inject;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.ReflectionUtils.*;

/**
 * Contains some utilities for testing dependency injection.
 */
public final class InjectionUtils {
	private InjectionUtils() {
	}

	/**
	 * Returns the component ID of the given object declared by a certain type.
	 * <p/>
	 * The ID must be declared by the given type.
	 * Inherited fields or fields of super classes are not checked.<br/>
	 * If {@code type} is {@code null}, the actual type of the object is used instead.
	 *
	 * @param component the object
	 * @param type      the type of the object to retrieve (may be {@code null})
	 * @return the component ID or {@code null} if none was found
	 * @see Class#getDeclaredFields()
	 */
	public static Long getId(Object component, Class<?> type) {
		Assert.notNull(AnnotationUtils.findAnnotation(component.getClass(), Component.class), "Object is not annotated with @Component: " + component);
		type = type != null ? type : component.getClass();
		for (Field field : type.getDeclaredFields()) {
			if (field.isAnnotationPresent(ComponentId.class)) {
				makeAccessible(field);
				return (Long) getField(field, component);
			}
		}
		return null;
	}

	/**
	 * Returns all component ID fields (including inherited and hidden) and their values of the given object.
	 *
	 * @param component the component to check
	 * @return the fields and their values
	 */
	public static Map<Field, Long> getIdsOfHierarchy(Object component) {
		Assert.notNull(AnnotationUtils.findAnnotation(component.getClass(), Component.class), "Object is not annotated with @Component: " + component);
		ComponentIdCallback callback = new ComponentIdCallback(component);
		doWithFields(component.getClass(), callback, new ComponentIdFilter());
		return callback.ids;
	}

	/**
	 * Traverses the given object graph and returns all component IDs.
	 *
	 * @param component the component to check
	 * @param map       the map to store the IDs
	 * @return the component IDs of the objects
	 * @see #getIdsOfHierarchy(Object)
	 */
	public static Map<Object, Map<Field, Long>> getIdsOfObjectGraph(final Object component, Map<Object, Map<Field, Long>> map) {
		map = map != null ? map : new HashMap<Object, Map<Field, Long>>();
		if (component != null && !map.containsKey(component)) {
			final Map<Object, Map<Field, Long>> finalMap = map;
			map.put(component, getIdsOfHierarchy(component));
			doWithFields(component.getClass(), new FieldCallback() {
				@Override
				public void doWith(Field field) {
					makeAccessible(field);
					Object value = getField(field, component);
					if (value != null) {
						finalMap.putAll(getIdsOfObjectGraph(value, finalMap));
					}
				}
			}, new InjectFilter());
		}
		return map;
	}

	/**
	 * Returns a list of all component IDs retrieved by {@link #getIdsOfObjectGraph(Object, java.util.Map)}.<br/>
	 *
	 * @param component the component to check
	 * @return the component IDs
	 */
	public static List<Long> getIds(Object component) {
		Map<Object, Map<Field, Long>> map = getIdsOfObjectGraph(component, null);
		List<Long> ids = new ArrayList<Long>();
		for (Map.Entry<Object, Map<Field, Long>> entry : map.entrySet()) {
			for (Map.Entry<Field, Long> id : entry.getValue().entrySet()) {
				ids.add(id.getValue());
			}
		}
		return ids;
	}

	/**
	 * Returns the names of all declared fields and their values of the given object declared by a certain type.
	 * <p/>
	 * The ID must be declared by the given type.
	 * Inherited fields or fields of super classes are not checked.<br/>
	 * If {@code type} is {@code null}, the actual type of the object is used instead.
	 *
	 * @param obj  the object
	 * @param type the type of the object to retrieve (may be {@code null})
	 * @return all declared field names and the field values
	 */
	public static Map<String, Object> getFields(Object obj, Class<?> type) {
		type = type != null ? type : obj.getClass();
		Map<String, Object> map = new HashMap<String, Object>();
		for (Field field : type.getDeclaredFields()) {
			makeAccessible(field);
			map.put(field.getName(), getField(field, obj));
		}
		return map;
	}

	/**
	 * Callback invoked on each component ID in the hierarchy.
	 */
	private static class ComponentIdCallback implements FieldCallback {
		protected final Map<Field, Long> ids = new HashMap<Field, Long>();
		protected Object component;

		private ComponentIdCallback(Object component) {
			this.component = component;
		}

		@Override
		public void doWith(Field field) throws IllegalAccessException {
			makeAccessible(field);
			ids.put(field, (Long) field.get(component));
		}

	}

	/**
	 * Callback used to filter component IDs.<br/>
	 * A component ID is a field of type {@link Long} annotated with {@link ComponentId @ComponentId}.
	 */
	private static class ComponentIdFilter implements FieldFilter {
		@Override
		public boolean matches(Field field) {
			return field.getType() == Long.class && field.isAnnotationPresent(ComponentId.class);
		}
	}

	/**
	 * Callback used to filter fields marked to be injected i.e., annotated with {@link Inject @Inject}.
	 */
	private static class InjectFilter implements FieldFilter {
		@Override
		public boolean matches(Field field) {
			return field.isAnnotationPresent(Inject.class);
		}
	}
}
