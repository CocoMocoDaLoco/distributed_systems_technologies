package dst.ass1.jpa.lifecycle;

import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;

import dst.ass1.jpa.model.ModelFactory;

public class EMLifecycleDemo {

	private EntityManager em;
	private ModelFactory modelFactory;

	public EMLifecycleDemo(EntityManager em, ModelFactory modelFactory) {
		this.em = em;
		this.modelFactory = modelFactory;
	}

	/**
	 * Method to illustrate the persistence lifecycle. EntityManager is opened and
	 * closed by the Test-Environment!
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public void demonstrateEntityMangerLifecycle()
			throws NoSuchAlgorithmException {

		// TODO
	}

}
