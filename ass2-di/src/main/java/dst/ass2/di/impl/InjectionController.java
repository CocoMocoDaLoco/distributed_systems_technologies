package dst.ass2.di.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;

import dst.ass2.di.IInjectionController;
import dst.ass2.di.InjectionException;
import dst.ass2.di.annotation.Component;

public class InjectionController implements IInjectionController {

    private final HashMap<Class<?>, Object> singletons =
            new HashMap<Class<?>, Object>();

    @Override
    public void initialize(Object obj) throws InjectionException {
        final Class<?> clazz = obj.getClass();
        assertComponent(clazz);

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

}
