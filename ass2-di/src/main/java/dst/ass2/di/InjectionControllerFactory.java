package dst.ass2.di;

import dst.ass2.di.impl.InjectionController;
import dst.ass2.di.impl.TransparentInjectionController;

/**
 * Creates and provides {@link IInjectionController} instances.
 */
public class InjectionControllerFactory {

    private static IInjectionController instance = null;
    private static IInjectionController transparentInstance = null;

    /**
     * Returns the singleton {@link IInjectionController} instance.<br/>
     * If none is available, a new one is created.
     *
     * @return the instance
     */
    public static synchronized IInjectionController getStandAloneInstance() {
        if (instance == null) {
            instance = getNewStandaloneInstance();
        }
        return instance;
    }

    /**
     * Returns the singleton {@link IInjectionController} instance for processing objects modified by an
     * {@link dst.ass2.di.agent.InjectorAgent InjectorAgent}.<br/>
     * If none is available, a new one is created.
     *
     * @return the instance
     */
    public static synchronized IInjectionController getTransparentInstance() {
        if (transparentInstance == null) {
            transparentInstance = getNewTransparentInstance();
        }
        return transparentInstance;
    }

    /**
     * Creates and returns a new {@link IInjectionController} instance.
     *
     * @return the newly created instance
     */
    public static IInjectionController getNewStandaloneInstance() {
        return new InjectionController();
    }

    /**
     * Creates and returns a new {@link IInjectionController} instance for processing objects modified by an
     * {@link dst.ass2.di.agent.InjectorAgent InjectorAgent}.<br/>
     *
     * @return the instance
     */
    public static IInjectionController getNewTransparentInstance() {
        return new TransparentInjectionController();
    }
}
