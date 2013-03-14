package dst.ass1.jpa.lifecycle;

import java.security.NoSuchAlgorithmException;

import javax.persistence.EntityManager;

import dst.ass1.jpa.model.IJob;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.ModelFactory;

public class EMLifecycleDemo {

    private final EntityManager em;
    private final ModelFactory modelFactory;

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
        /*
         * Entity instances are in one of four states: new, managed, detached, or removed.
         *
         * New entity instances have no persistent identity and are not yet associated with a persistence
         * context.
         * Managed entity instances have a persistent identity and are associated with a persistence
         * context.
         * Detached entity instances have a persistent identity and are not currently associated with a
         * persistence context.
         * Removed entity instances have a persistent identity, are associated with a persistent context,
         * and are scheduled for removal from the data store.
         */

        /* New. */

        IUser user = modelFactory.createUser();

        IJob job = modelFactory.createJob();
        job.setUser(user);

        /* Managed.
         *
         * New entity instances become managed and persistent either by
         * invoking the persist method or by a cascading persist operation
         * invoked from related entities that have the cascade=PERSIST or
         * cascade=ALL elements set in the relationship annotation. This means
         * that the entity’s data is stored to the database when the
         * transaction associated with the persist operation is completed. If
         * the entity is already managed, the persist operation is ignored,
         * although the persist operation will cascade to related entities that
         * have the cascade element set to PERSIST or ALL in the relationship
         * annotation. If persist is called on a removed entity instance, the
         * entity becomes managed. If the entity is detached, either persist
         * will throw an IllegalArgumentException, or the transaction commit
         * will fail.
         * The persist operation is propagated to all entities related to the
         * calling entity that have the cascade element set to ALL or PERSIST
         * in the relationship annotation.
         */

        em.persist(job);

        /* Removed.
         *
         * Managed entity instances are removed by invoking the remove method
         * or by a cascading remove operation invoked from related entities
         * that have the cascade=REMOVE or cascade=ALL elements set in the
         * relationship annotation. If the remove method is invoked on a new
         * entity, the remove operation is ignored, although remove will
         * cascade to related entities that have the cascade element set to
         * REMOVE or ALL in the relationship annotation. If remove is invoked
         * on a detached entity, either remove will throw an
         * IllegalArgumentException, or the transaction commit will fail. If
         * invoked on an already removed entity, remove will be ignored. The
         * entity’s data will be removed from the data store when the
         * transaction is completed or as a result of the flush operation.
         */

        em.remove(job);

        /* Detached. */

        em.detach(job);
    }

}
