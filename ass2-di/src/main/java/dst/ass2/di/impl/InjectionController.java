package dst.ass2.di.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import dst.ass2.di.IInjectionController;
import dst.ass2.di.InjectionException;
import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;

public class InjectionController implements IInjectionController {

    private final HashMap<Class<?>, Object> singletons =
            new HashMap<Class<?>, Object>();

    private final HashMap<Class<?>, Long> ids = new HashMap<Class<?>, Long>();

    @Override
    public void initialize(Object obj) throws InjectionException {
        boolean componentFound = false;
        boolean idFound = false;
        final Long id = getAndIncrementId(obj.getClass());

        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (!isComponent(clazz)) {
                continue;
            }

            componentFound = true;
            idFound |= initializeClass(obj, clazz, id);
        }

        if (!componentFound) {
            throw new InjectionException("No Component found");
        }

        if (!idFound) {
            throw new InjectionException("No ComponentId found");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSingletonInstance(Class<T> clazz) throws InjectionException {
        isComponent(clazz);

        synchronized(singletons) {
            if (!singletons.containsKey(clazz)) {
                try {
                    T instance = clazz.newInstance();
                    initialize(instance);
                    singletons.put(clazz, instance);
                } catch (InstantiationException e) {
                    throw new InjectionException(e);
                } catch (IllegalAccessException e) {
                    throw new InjectionException(e);
                }
            }

            return (T)singletons.get(clazz);
        }
    }

    private static boolean isComponent(Class<?> clazz) {
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (annotation.annotationType() == Component.class) {
                return true;
            }
        }

        return false;
    }

    private void injectId(Long id, Object obj, Field field) {
        if (field.getType() != Long.class) {
            throw new InjectionException("Id must be of type Long");
        }

        final boolean wasAccessible = field.isAccessible();

        try {
            field.setAccessible(true);
            field.set(obj, id);
        } catch (IllegalArgumentException e) {
            throw new InjectionException(e);
        } catch (IllegalAccessException e) {
            throw new InjectionException(e);
        } finally {
            field.setAccessible(wasAccessible);
        }
    }

    private Long getAndIncrementId(Class<?> clazz) {
        synchronized (ids) {
            Long id = ids.get(clazz);
            if (id == null) {
                id = 0L;
            }
            ids.put(clazz, id + 1);
            return id;
        }
    }

    private boolean initializeClass(Object obj, final Class<?> clazz, final Long id) {
        boolean idFound = false;

        for (Field field : clazz.getDeclaredFields()) {
            final String fname = field.getName();

            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType() == ComponentId.class) {
                    injectId(id, obj, field);
                    idFound = true;
                }
            }
        }

        return idFound;
    }

}
