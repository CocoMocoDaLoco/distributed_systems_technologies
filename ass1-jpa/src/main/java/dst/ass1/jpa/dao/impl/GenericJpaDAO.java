package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.GenericDAO;

public abstract class GenericJpaDAO<T> implements GenericDAO<T> {

    private final Session session;

    public GenericJpaDAO(Session session) {
        this.session = session;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T findById(Long id) {
        String query = String.format("from %s where %s = :id",
                getTableName(),
                getIdColumnName());

        List<T> l = session.createQuery(query).setParameter("id", id).list();

        if (l.isEmpty()) {
            return null;
        }

        return l.get(0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<T> findAll() {
        String query = String.format("from %s", getTableName());
        return session.createQuery(query).list();
    }

    protected abstract String getTableName();

    protected String getIdColumnName() {
        return "id";
    }

    protected Session getSession() {
        return session;
    }

}
