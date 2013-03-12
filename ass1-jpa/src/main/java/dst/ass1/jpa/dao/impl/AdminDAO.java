package dst.ass1.jpa.dao.impl;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IAdminDAO;
import dst.ass1.jpa.model.IAdmin;
import dst.ass1.jpa.model.impl.Admin;

public class AdminDAO extends GenericJpaDAO<IAdmin> implements IAdminDAO {

    public AdminDAO(Session session) {
        super(session);
    }

    @Override
    protected String getTableName() {
        return Admin.class.getSimpleName();
    }

}
