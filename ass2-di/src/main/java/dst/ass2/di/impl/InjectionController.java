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

    @Override
    public void initialize(Object obj) throws InjectionException {
        final Class<?> clazz = obj.getClass();
        assertComponent(clazz);

        boolean idFound = false;

        for (Field field : clazz.getDeclaredFields()) {
            final String fname = field.getName();

            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation.annotationType() == ComponentId.class) {
                    injectId(obj, field);
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

    private void injectId(Object obj, Field field) {
        if (field.getType() != Long.class) {
            throw new InjectionException("Id must be of type Long");
        }
    }

}
