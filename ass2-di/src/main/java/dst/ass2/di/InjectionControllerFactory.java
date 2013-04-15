package dst.ass2.di;

/**
 * Creates and provides {@link IInjectionController} instances.
 */
public class InjectionControllerFactory {


	/**
	 * Returns the singleton {@link IInjectionController} instance.<br/>
	 * If none is available, a new one is created.
	 *
	 * @return the instance
	 */
	public static synchronized IInjectionController getStandAloneInstance() {
		// TODO
		return null;
	}

	/**
	 * Returns the singleton {@link IInjectionController} instance for processing objects modified by an
	 * {@link dst.ass2.di.agent.InjectorAgent InjectorAgent}.<br/>
	 * If none is available, a new one is created.
	 *
	 * @return the instance
	 */
	public static synchronized IInjectionController getTransparentInstance() {
		// TODO
		return null;
	}

	/**
	 * Creates and returns a new {@link IInjectionController} instance.
	 *
	 * @return the newly created instance
	 */
	public static IInjectionController getNewStandaloneInstance() {
		// TODO
		return null;
	}

	/**
	 * Creates and returns a new {@link IInjectionController} instance for processing objects modified by an
	 * {@link dst.ass2.di.agent.InjectorAgent InjectorAgent}.<br/>
	 *
	 * @return the instance
	 */
	public static IInjectionController getNewTransparentInstance() {
		// TODO
		return null;
	}
}
