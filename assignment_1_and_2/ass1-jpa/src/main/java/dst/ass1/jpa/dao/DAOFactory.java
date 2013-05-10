package dst.ass1.jpa.dao;

import dst.ass1.jpa.dao.impl.AdminDAO;
import dst.ass1.jpa.dao.impl.ClusterDAO;
import dst.ass1.jpa.dao.impl.ComputerDAO;
import dst.ass1.jpa.dao.impl.EnvironmentDAO;
import dst.ass1.jpa.dao.impl.ExecutionDAO;
import dst.ass1.jpa.dao.impl.GridDAO;
import dst.ass1.jpa.dao.impl.JobDAO;
import dst.ass1.jpa.dao.impl.MembershipDAO;
import dst.ass1.jpa.dao.impl.UserDAO;

public class DAOFactory {

    private final org.hibernate.Session session;

    public DAOFactory(org.hibernate.Session session) {
        this.session = session;
    }

    public IGridDAO getGridDAO() {
        return new GridDAO(session);
    }

    public IAdminDAO getAdminDAO() {
        return new AdminDAO(session);
    }

    public IClusterDAO getClusterDAO() {
        return new ClusterDAO(session);
    }

    public IComputerDAO getComputerDAO() {
        return new ComputerDAO(session);
    }

    public IEnvironmentDAO getEnvironmentDAO() {
        return new EnvironmentDAO(session);
    }

    public IExecutionDAO getExecutionDAO() {
        return new ExecutionDAO(session);
    }

    public IJobDAO getJobDAO() {
        return new JobDAO(session);
    }

    public IMembershipDAO getMembershipDAO() {
        return new MembershipDAO(session);
    }

    public IUserDAO getUserDAO() {
        return new UserDAO(session);
    }

}
