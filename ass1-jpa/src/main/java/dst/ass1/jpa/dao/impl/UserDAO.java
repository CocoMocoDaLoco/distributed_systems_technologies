package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IUserDAO;
import dst.ass1.jpa.model.IUser;

public class UserDAO implements IUserDAO {

    public UserDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IUser findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IUser> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
