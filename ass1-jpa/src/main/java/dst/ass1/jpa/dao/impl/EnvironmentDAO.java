package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IEnvironmentDAO;
import dst.ass1.jpa.model.IEnvironment;

public class EnvironmentDAO implements IEnvironmentDAO {

    public EnvironmentDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IEnvironment findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IEnvironment> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
