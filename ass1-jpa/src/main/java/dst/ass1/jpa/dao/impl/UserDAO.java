package dst.ass1.jpa.dao.impl;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IUserDAO;
import dst.ass1.jpa.model.IUser;
import dst.ass1.jpa.model.impl.User;

public class UserDAO extends GenericJpaDAO<IUser> implements IUserDAO {

    public UserDAO(Session session) {
        super(session);
    }

    @Override
    protected String getTableName() {
        return User.class.getSimpleName();
    }

}
