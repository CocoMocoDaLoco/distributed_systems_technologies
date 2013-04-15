package dst.ass2.di;

/**
 * The dependency injection controller interface.
 */
public interface IInjectionController {

    /**
     * Injects all fields annotated with {@link dst.ass2.di.annotation.Inject @Inject}.<br/>
     * Only objects of classes annotated with {@link dst.ass2.di.annotation.Component @Component} shall be accepted.
	 *
     * @param obj the object to initialize.
     * @throws InjectionException if it's no component or dependency injection fails.
     */
    void initialize(Object obj) throws InjectionException;

    /**
     * Gets the fully initialized instance of a singleton component.<br/>
     * Multiple calls of this method always have to return the same instance.
     * Only classes that are annotated with {@link dst.ass2.di.annotation.Component Component} shall be accepted.
	 *
     * @param <T> the class type.
     * @param clazz the class of the component.
     * @return the initialized singleton object.
     * @throws InjectionException if it's no component or dependency injection fails.
     */
    <T> T getSingletonInstance(Class<T> clazz) throws InjectionException;
}
