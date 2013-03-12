package dst.ass1.jpa.dao.impl;

import org.hibernate.Session;

import dst.ass1.jpa.dao.IMembershipDAO;
import dst.ass1.jpa.model.IMembership;
import dst.ass1.jpa.model.impl.Membership;

public class MembershipDAO extends GenericJpaDAO<IMembership> implements IMembershipDAO {

    public MembershipDAO(Session session) {
        super(session);
    }

    @Override
    public IMembership findById(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getTableName() {
        return Membership.class.getSimpleName();
    }

}
