package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IEnvironmentDAO;
import dst.ass1.jpa.model.IEnvironment;

public class EnvironmentDAO implements IEnvironmentDAO {

    private final Session session;

    public EnvironmentDAO(Session session) {
        this.session = session;
    }

    @Override
    public IEnvironment findById(Long id) {
        List<IEnvironment> l = null;

        session.beginTransaction();
        l = session.createQuery("from Environment where id = :id").setParameter("id", id).list();
        session.getTransaction().commit();

        if (l.isEmpty()) {
            return null;
        }

        return l.get(0);
    }

    @Override
    public List<IEnvironment> findAll() {
        List<IEnvironment> l = null;

        session.beginTransaction();
        l = session.createQuery("from Environment").list();
        session.getTransaction().commit();

        return l;
    }

}
