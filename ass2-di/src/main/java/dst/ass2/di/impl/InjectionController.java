package dst.ass2.di.impl;

import java.util.HashMap;

import dst.ass2.di.IInjectionController;
import dst.ass2.di.InjectionException;

public class InjectionController implements IInjectionController {

    private final HashMap<Class<?>, Object> singletons =
            new HashMap<Class<?>, Object>();

    @Override
    public void initialize(Object obj) throws InjectionException {
        // TODO Auto-generated method stub

    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getSingletonInstance(Class<T> clazz) throws InjectionException {
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

}
