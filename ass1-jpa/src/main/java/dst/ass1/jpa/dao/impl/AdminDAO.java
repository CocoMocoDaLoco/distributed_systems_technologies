package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IAdminDAO;
import dst.ass1.jpa.model.IAdmin;

public class AdminDAO implements IAdminDAO {

    public AdminDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IAdmin findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IAdmin> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
