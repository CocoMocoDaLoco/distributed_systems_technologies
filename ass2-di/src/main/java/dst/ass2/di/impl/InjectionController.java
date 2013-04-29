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
        final Class<?> clazz = obj.getClass();
        assertComponent(clazz);

        boolean idFound = false;
        final Long id = getAndIncrementId(clazz);

        for (Field field : clazz.getDeclaredFields()) {
            final String fname = field.getName();

            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType() == ComponentId.class) {
                    injectId(id, obj, field);
                    idFound = true;
                }
            }
        }

        if (!idFound) {
            throw new InjectionException("No ComponentId found");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSingletonInstance(Class<T> clazz) throws InjectionException {
        assertComponent(clazz);

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

    private static void assertComponent(Class<?> clazz) {
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (annotation.annotationType() == Component.class) {
                return;
            }
        }

        throw new InjectionException("Not a component");
    }

    private void injectId(Long id, Object obj, Field field) {
        if (field.getType() != Long.class) {
            throw new InjectionException("Id must be of type Long");
        }

        try {
            field.set(obj, id);
        } catch (IllegalArgumentException e) {
            throw new InjectionException(e);
        } catch (IllegalAccessException e) {
            throw new InjectionException(e);
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

}
