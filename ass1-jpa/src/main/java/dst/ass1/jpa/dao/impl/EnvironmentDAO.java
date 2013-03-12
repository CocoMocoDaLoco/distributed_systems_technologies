package dst.ass1.jpa.dao.impl;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IEnvironmentDAO;
import dst.ass1.jpa.model.IEnvironment;

public class EnvironmentDAO extends GenericJpaDAO<IEnvironment> implements IEnvironmentDAO {

    public EnvironmentDAO(Session session) {
        super(session);
    }

    @Override
    protected String getTableName() {
        return "Environment";
    }

}
