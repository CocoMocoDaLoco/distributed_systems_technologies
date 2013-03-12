package dst.ass1.jpa.dao.impl;

import java.util.List;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IMembershipDAO;
import dst.ass1.jpa.model.IMembership;

public class MembershipDAO implements IMembershipDAO {

    public MembershipDAO(Session session) {
        // TODO Auto-generated constructor stub
    }

    @Override
    public IMembership findById(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<IMembership> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

}
