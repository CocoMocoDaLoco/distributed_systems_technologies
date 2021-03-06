package dst.ass2.di.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import dst.ass2.di.IInjectionController;
import dst.ass2.di.InjectionException;
import dst.ass2.di.annotation.Component;
import dst.ass2.di.annotation.ComponentId;
import dst.ass2.di.annotation.Inject;
import dst.ass2.di.model.ScopeType;

public class InjectionController implements IInjectionController {

    private final HashMap<Class<?>, Object> singletons =
            new HashMap<Class<?>, Object>();

    private final AtomicLong nextId = new AtomicLong(0L);

    @Override
    public void initialize(Object obj) throws InjectionException {
        Class<?> clazz = obj.getClass();

        /* The specification is unclear on how to handle singletons in
         * the inheritance hierarchy. */
        if (isSingleton(clazz)) {
            synchronized (singletons) {
                if (singletons.containsKey(clazz)) {
                    throw new InjectionException("Duplicate Singleton instance");
                }
                singletons.put(clazz, obj);
            }
        }

        boolean componentFound = false;
        boolean idFound = false;
        final Long id = nextId.getAndIncrement();

        Map<String, Annotation> names = new HashMap<String, Annotation>();

        for (clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (!isComponent(clazz)) {
                continue;
            }

            componentFound = true;
            idFound |= initializeClass(obj, clazz, id, names);
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
        if (!isSingleton(clazz)) {
            throw new InjectionException("No singleton Component found");
        }

        synchronized(singletons) {
            if (!singletons.containsKey(clazz)) {
                try {
                    T instance = clazz.newInstance();
                    initialize(instance);
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

    private static boolean isSingleton(Class<?> clazz) {
        for (Annotation annotation : clazz.getDeclaredAnnotations()) {
            if (annotation.annotationType() == Component.class) {
                Component c = (Component)annotation;
                return (c.scope() == ScopeType.SINGLETON);
            }
        }

        return false;
    }

    private static void injectId(Long id, Object obj, Field field) {
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

    private void inject(Object obj, Field field, Inject annotation) {
        final boolean required = annotation.required();
        final Exception e = tryInject(obj, field, annotation);

        if (e != null && required) {
            throw new InjectionException(e);
        }
    }

    private Exception tryInject(Object obj, Field field, Inject annotation) {
        final Class<?> type = (annotation.specificType() == Void.class) ?
                field.getType() : annotation.specificType();

        if (!isComponent(type)) {
            return new InjectionException("No Component found");
        }

        final boolean wasAccessible = field.isAccessible();

        try {
            Object inj = null;
            if (isSingleton(type)) {
                inj = getSingletonInstance(type);
            } else {
                inj = type.newInstance();
                initialize(inj);
            }

            field.setAccessible(true);
            field.set(obj, inj);
        } catch (Exception e) {
            return e;
        } finally {
            field.setAccessible(wasAccessible);
        }

        return null;
    }

    private boolean initializeClass(Object obj, final Class<?> clazz, final Long id, Map<String, Annotation> names) {
        boolean idFound = false;

        for (Field field : clazz.getDeclaredFields()) {
            Annotation processedAnnotation = null;
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                final Class<?> annotationType = annotation.annotationType();
                if (annotationType == ComponentId.class) {
                    injectId(id, obj, field);
                    idFound = true;
                } else if (annotationType == Inject.class) {
                    processedAnnotation = annotation;
                    inject(obj, field, (Inject)annotation);
                }
            }

            if (isAmbiguousName(names, field.getName(), processedAnnotation)) {
                System.err.printf("Ambiguous name %s in class %s%n", field.getName(), clazz.getName());
            }
            names.put(field.getName(), processedAnnotation);
        }

        return idFound;
    }

    private static boolean isAmbiguousName(Map<String, Annotation> names, String name,
            Annotation annotation) {
        if (!names.containsKey(name)) {
            return false;
        }

        final Inject otherAnnotation = (Inject)names.get(name);
        final Inject thisAnnotation = (Inject)annotation;

        if (otherAnnotation == null && thisAnnotation == null) {
            return false;
        }

        if (otherAnnotation == null || thisAnnotation == null) {
            return true;
        }

        if (otherAnnotation.required() != thisAnnotation.required() || otherAnnotation.specificType() != thisAnnotation.specificType()) {
            return true;
        }

        return false;
    }

}
